package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.utils.ByteUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
const EVENT_QUEUE_HEADER = struct([
  blob(5), 0-5

  accountFlagsLayout('accountFlags'), 5-12
  u32('head'), 12-16
  zeros(4), 16-20
  u32('count'), 20-24
  zeros(4), 24-28
  u32('seqNum'), 28-32
  zeros(4), 32-36
]);
 */

/*
const EVENT_FLAGS = bits(u8(), false, 'eventFlags');
EVENT_FLAGS.addBoolean('fill');
EVENT_FLAGS.addBoolean('out');
EVENT_FLAGS.addBoolean('bid');
EVENT_FLAGS.addBoolean('maker');

const EVENT = struct([
  EVENT_FLAGS,
  u8('openOrdersSlot'),
  u8('feeTier'),
  blob(5),
  u64('nativeQuantityReleased'), // Amount the user received
  u64('nativeQuantityPaid'), // Amount the user paid
  u64('nativeFeeOrRebate'),
  u128('orderId'),
  publicKeyLayout('openOrders'),
  u64('clientOrderId'),
]);
 */
public class EventQueue {

    private static final Logger LOGGER = Logger.getLogger(EventQueue.class.getName());

    // sizes
    private static final int HEADER_LAYOUT_SPAN = 37;
    private static final int NODE_LAYOUT_SPAN = 88;

    // offsets
    private static final int HEAD_OFFSET = 13;
    private static final int COUNT_OFFSET = 21;
    private static final int SEQ_NUM_OFFSET = 29;

    private AccountFlags accountFlags;
    private int head;
    private int count;
    private int seqNum;
    private List<TradeEvent> events;
    private List<PublicKey> topTraders = new ArrayList<>();

    /**
     * Returns an {@link EventQueue} object which is built from binary data.
     *
     * @param eventQueueData binary data
     * @return built {@link EventQueue} object
     */
    public static EventQueue readEventQueue(byte[] eventQueueData, RpcClient client) {
        EventQueue eventQueue = new EventQueue();
        List<TradeEvent> events = new ArrayList<>();
        eventQueue.setEvents(events);

        // Verify that the "serum" padding exists
        SerumUtils.validateSerumData(eventQueueData);

        // Read account flags
        AccountFlags accountFlags = AccountFlags.readAccountFlags(eventQueueData);
        eventQueue.setAccountFlags(accountFlags);

        // Read rest of EVENT_QUEUE_HEADER (head, count, seqNum ints)
        int head = (int) Utils.readUint32(eventQueueData, HEAD_OFFSET);
        int count = (int) Utils.readUint32(eventQueueData, COUNT_OFFSET);
        int seqNum = (int) Utils.readUint32(eventQueueData, SEQ_NUM_OFFSET);

        eventQueue.setHead(head);
        eventQueue.setCount(count);
        eventQueue.setSeqNum(seqNum);

        // allocLen = number of elements
        int allocLen = (eventQueueData.length - HEADER_LAYOUT_SPAN) / NODE_LAYOUT_SPAN;

//        LOGGER.info(String.format("allocLen = %d", allocLen));
//        LOGGER.info(String.format("Head = %d, Count = %d, seqNum = %d", head, count, seqNum));

        for (int i = 0; i < allocLen; ++i) {
            int nodeIndex = (head + count + allocLen - 1 - i) % allocLen;
            int eventOffset = HEADER_LAYOUT_SPAN + (nodeIndex * NODE_LAYOUT_SPAN);
//            LOGGER.info(
//                String.format(
//                        "HPush (%d) (offset %d): nodeIndex = %d, headerLayout.span = %d, nodeLayout.span = %d",
//                        i,
//                        eventOffset,
//                        nodeIndex,
//                        HEADER_LAYOUT_SPAN,
//                        NODE_LAYOUT_SPAN
//                )
//            );

            // read in 88 bytes of event queue data
            byte[] eventData = Arrays.copyOfRange(eventQueueData, eventOffset, eventOffset + NODE_LAYOUT_SPAN);
            byte eventFlags = eventData[0];
            boolean fill = (eventFlags & 1) == 1;
            boolean out = (eventFlags & 2) == 2;
            boolean bid = (eventFlags & 4) == 4;
            boolean maker = (eventFlags & 8) == 8;

            EventQueueFlags eventQueueFlags = new EventQueueFlags(fill, out, bid, maker);

            byte openOrdersSlot = eventData[1];
            byte feeTier = eventData[2];

            // blob = 3-7 - ignore
            // Amount the user received
            long nativeQuantityReleased = ByteUtils.readUint64(eventData, 8).longValue();

            // Amount the user paid
            long nativeQuantityPaid = ByteUtils.readUint64(eventData, 16).longValue();

            long nativeFeeOrRebate = ByteUtils.readUint64(eventData, 24).longValue();
            byte[] orderId = Arrays.copyOfRange(eventData, 32, 48);
            PublicKey openOrders = PublicKey.readPubkey(eventData, 48);
            long clientOrderId = ByteUtils.readUint64(eventData, 80).longValue();

            if (fill && nativeQuantityPaid > 0) {
                TradeEvent tradeEvent = new TradeEvent();
                tradeEvent.setOpenOrders(openOrders);
                tradeEvent.setNativeQuantityPaid(nativeQuantityPaid);
                tradeEvent.setOrderId(orderId);
                tradeEvent.setEventQueueFlags(eventQueueFlags);

                eventQueue.getEvents().add(tradeEvent);
            }
        }

        List<String> publicKeys = events.stream()
                .map(tradeEvent -> tradeEvent.getOpenOrders().toBase58())
                .sorted()
                .collect(Collectors.toList());

        Map<String, Integer> counter = new HashMap<>();

        publicKeys.forEach(publicKey -> {
            int value = counter.getOrDefault(publicKey, 0) + 1;
            counter.put(publicKey, value);
        });

        final List<PublicKey> sortedMarketMakers = counter.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .map(stringIntegerEntry -> new PublicKey(stringIntegerEntry.getKey())).distinct().collect(Collectors.toList());

        eventQueue.getTopTraders().clear();

        for (int i = 0; i < 5; i++) {
            try {
                PublicKey sortedMarketMaker = sortedMarketMakers.get(i);
                byte[] bytes = Base64.getDecoder().decode(client.getApi().getAccountInfo(sortedMarketMaker).getValue().getData().get(0));
                PublicKey owner = PublicKey.readPubkey(bytes, 45);
                eventQueue.getTopTraders().add(owner);
                LOGGER.info(String.format("Rank #%d Market Maker = %s, Owner = %s (https://explorer.solana.com/address/%s)", i + 1, sortedMarketMaker.toBase58(), owner.toBase58(), owner.toBase58()));
            } catch (RpcException e) {
                e.printStackTrace();
            }
        }

//        counter.entrySet().stream()
//                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
//                .forEach(k -> {
//                    LOGGER.info(String.format("Open Orders Account: %s, Number of Event Queue fills: %d\nExplorer: https://explorer.solana.com/address/%s", k.getKey(), k.getValue(), k.getKey()));
//                });

        return eventQueue;
    }

    public AccountFlags getAccountFlags() {
        return accountFlags;
    }

    public void setAccountFlags(AccountFlags accountFlags) {
        this.accountFlags = accountFlags;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public List<TradeEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TradeEvent> events) {
        this.events = events;
    }

    public List<PublicKey> getTopTraders() {
        return topTraders;
    }

    public void setTopTraders(List<PublicKey> topTraders) {
        this.topTraders = topTraders;
    }
}
