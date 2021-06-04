package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.util.Arrays;

/**
 * Represents a Trade Event that occurs inside of a Serum {@link EventQueue}
 */
public class TradeEvent {

    private PublicKey openOrders;
    private long nativeQuantityPaid;
    private byte[] orderId;
    private EventQueueFlags eventQueueFlags;
    private byte openOrdersSlot;
    private byte feeTier;
    private long nativeQuantityReleased;
    private long nativeFeeOrRebate;
    private long clientOrderId;

    private float floatPrice;
    private float floatQuantity;



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

    public byte getOpenOrdersSlot() {
        return openOrdersSlot;
    }

    public void setOpenOrdersSlot(byte openOrdersSlot) {
        this.openOrdersSlot = openOrdersSlot;
    }

    public byte getFeeTier() {
        return feeTier;
    }

    public void setFeeTier(byte feeTier) {
        this.feeTier = feeTier;
    }

    public long getNativeQuantityReleased() {
        return nativeQuantityReleased;
    }

    public void setNativeQuantityReleased(long nativeQuantityReleased) {
        this.nativeQuantityReleased = nativeQuantityReleased;
    }

    public long getNativeFeeOrRebate() {
        return nativeFeeOrRebate;
    }

    public void setNativeFeeOrRebate(long nativeFeeOrRebate) {
        this.nativeFeeOrRebate = nativeFeeOrRebate;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(long clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public float getFloatPrice() {
        return floatPrice;
    }

    public void setFloatPrice(float floatPrice) {
        this.floatPrice = floatPrice;
    }

    public float getFloatQuantity() {
        return floatQuantity;
    }

    public void setFloatQuantity(float floatQuantity) {
        this.floatQuantity = floatQuantity;
    }

    @Override
    public String toString() {
        return "TradeEvent{" +
                "openOrders=" + openOrders +
                ", nativeQuantityPaid=" + nativeQuantityPaid +
                ", orderId=" + Arrays.toString(orderId) +
                ", eventQueueFlags=" + eventQueueFlags +
                ", openOrdersSlot=" + openOrdersSlot +
                ", feeTier=" + feeTier +
                ", nativeQuantityReleased=" + nativeQuantityReleased +
                ", nativeFeeOrRebate=" + nativeFeeOrRebate +
                ", clientOrderId=" + clientOrderId +
                ", floatPrice=" + floatPrice +
                ", floatQuantity=" + floatQuantity +
                '}';
    }
}
