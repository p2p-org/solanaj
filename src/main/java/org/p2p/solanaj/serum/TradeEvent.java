package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.util.Arrays;

public class TradeEvent {

    private PublicKey openOrders;
    private long nativeQuantityPaid;
    private byte[] orderId;

    public TradeEvent() {

    }

    public TradeEvent(PublicKey openOrders, long nativeQuantityPaid, byte[] orderId) {
        this.openOrders = openOrders;
        this.nativeQuantityPaid = nativeQuantityPaid;
        this.orderId = orderId;
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

    @Override
    public String toString() {
        return "TradeEvent{" +
                "openOrders=" + openOrders +
                ", nativeQuantityPaid=" + nativeQuantityPaid +
                ", orderId=" + Arrays.toString(orderId) +
                '}';
    }
}
