package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.util.Arrays;

public class TradeEvent {

    private PublicKey openOrders;
    private long nativeQuantityPaid;
    private byte[] orderId;
    private EventQueueFlags eventQueueFlags;

    public TradeEvent() {

    }

    public TradeEvent(PublicKey openOrders, long nativeQuantityPaid, byte[] orderId, EventQueueFlags eventQueueFlags) {
        this.openOrders = openOrders;
        this.nativeQuantityPaid = nativeQuantityPaid;
        this.orderId = orderId;
        this.eventQueueFlags = eventQueueFlags;
    }

    public PublicKey getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(PublicKey openOrders) {
        this.openOrders = openOrders;
    }

    public long getNativeQuantityPaid() {
        return nativeQuantityPaid;
    }

    public void setNativeQuantityPaid(long nativeQuantityPaid) {
        this.nativeQuantityPaid = nativeQuantityPaid;
    }

    public byte[] getOrderId() {
        return orderId;
    }

    public void setOrderId(byte[] orderId) {
        this.orderId = orderId;
    }

    public EventQueueFlags getEventQueueFlags() {
        return eventQueueFlags;
    }

    public void setEventQueueFlags(EventQueueFlags eventQueueFlags) {
        this.eventQueueFlags = eventQueueFlags;
    }

    @Override
    public String toString() {
        return "TradeEvent{" +
                "openOrders=" + openOrders +
                ", nativeQuantityPaid=" + nativeQuantityPaid +
                ", orderId=" + Arrays.toString(orderId) +
                ", eventQueueFlags=" + eventQueueFlags +
                '}';
    }
}
