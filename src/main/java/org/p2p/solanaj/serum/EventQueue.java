package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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

    /**
     * Returns an {@link EventQueue} object which is built from binary data.
     *
     * @param eventQueueData binary data
     * @return built {@link EventQueue} object
     */
    public static EventQueue readEventQueue(byte[] eventQueueData) {
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

        LOGGER.info(String.format("allocLen = %d", allocLen));
        LOGGER.info(String.format("Head = %d, Count = %d, seqNum = %d", head, count, seqNum));

        for (int i = 0; i < allocLen; ++i) {
            int nodeIndex = (head + count + allocLen - 1 - i) % allocLen;
            int eventOffset = HEADER_LAYOUT_SPAN + (nodeIndex * NODE_LAYOUT_SPAN);
            LOGGER.info(
                String.format(
                        "HPush (%d) (offset %d): nodeIndex = %d, headerLayout.span = %d, nodeLayout.span = %d",
                        i,
                        eventOffset,
                        nodeIndex,
                        HEADER_LAYOUT_SPAN,
                        NODE_LAYOUT_SPAN
                )
            );

            // read in 88 bytes of event queue data
            byte[] eventData = Arrays.copyOfRange(eventQueueData, eventOffset, eventOffset + NODE_LAYOUT_SPAN);
            byte eventFlags = eventData[0];

            boolean fill = (eventFlags & 1) == 1;
            boolean out = (eventFlags & 2) == 2;
            boolean bid = (eventFlags & 4) == 4;
            boolean maker = (eventFlags & 8) == 8;

            LOGGER.info(
                    String.format(
                            "Event flags (%d): Fill (%s), Out (%s), Bid (%s), Maker (%s)",
                            eventFlags,
                            fill,
                            out,
                            bid,
                            maker
                    )
            );

            byte openOrdersSlot = eventData[1];
            byte feeTier = eventData[2];

            LOGGER.info(
                    String.format(
                            "openOrdersSlot = %d, feeTier = %d",
                            openOrdersSlot,
                            feeTier
                    )
            );

            // blob = 3-7 - ignore
            long nativeQuantityReleased = ByteUtils.readUint64(eventData, 8).longValue(); // Amount the user received
            long nativeQuantityPaid = ByteUtils.readUint64(eventData, 16).longValue(); // Amount the user paid
            long nativeFeeOrRebate = ByteUtils.readUint64(eventData, 24).longValue();
            byte[] orderId = Arrays.copyOfRange(eventData, 32, 48);
            PublicKey openOrders = PublicKey.readPubkey(eventData, 48);
            long clientOrderId = ByteUtils.readUint64(eventData, 80).longValue();

            LOGGER.info(
                    String.format(
                            "nativeQuantityReleased = %d, nativeQuantityPaid = %d, nativeFeeRebate = %d, " +
                                    "orderId = %s, openOrders = %s, clientOrderId = %d",
                            nativeQuantityReleased,
                            nativeQuantityPaid,
                            nativeFeeOrRebate,
                            Arrays.toString(orderId),
                            openOrders,
                            clientOrderId
                    )
            );

            if (fill && nativeQuantityPaid > 0) {
                eventQueue.getEvents().add(new TradeEvent(openOrders, nativeQuantityPaid, orderId));
            }
        }

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
}
