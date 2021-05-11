package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;

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

    /**
     * Returns an {@link EventQueue} object which is built from binary data.
     *
     * @param eventQueueData binary data
     * @return built {@link EventQueue} object
     */
    public static EventQueue readEventQueue(byte[] eventQueueData) {
        EventQueue eventQueue = new EventQueue();

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
            LOGGER.info(String.format("HPush: nodeIndex = %d, headerLayout.span = %d, nodeLayout.span = %d", nodeIndex, HEADER_LAYOUT_SPAN, NODE_LAYOUT_SPAN));
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
}
