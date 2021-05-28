package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

/**
 * Class that represents a Serum order.
 */
public class Order {

    private long price;
    private long quantity;
    private long clientOrderId;
    private float floatPrice;
    private float floatQuantity;
    private PublicKey owner;

    // used in newOrderv3. no constructor, only setters/getters
    private long maxQuoteQuantity;
    private long clientId;
    private OrderTypeLayout orderTypeLayout;
    private SelfTradeBehaviorLayout selfTradeBehaviorLayout;
    private boolean buy;

    // constructor used by new orders
    public Order(float floatPrice, float floatQuantity, long clientOrderId) {
        this.floatPrice = floatPrice;
        this.floatQuantity = floatQuantity;
        this.clientOrderId = clientOrderId;
    }

    public Order(long price, long quantity, long clientOrderId, float floatPrice, float floatQuantity, PublicKey owner) {
        this.price = price;
        this.quantity = quantity;
        this.clientOrderId = clientOrderId;
        this.floatPrice = floatPrice;
        this.floatQuantity = floatQuantity;
        this.owner = owner;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
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

    public PublicKey getOwner() {
        return owner;
    }

    public void setOwner(PublicKey owner) {
        this.owner = owner;
    }

    public long getMaxQuoteQuantity() {
        return maxQuoteQuantity;
    }

    public void setMaxQuoteQuantity(long maxQuoteQuantity) {
        this.maxQuoteQuantity = maxQuoteQuantity;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public OrderTypeLayout getOrderTypeLayout() {
        return orderTypeLayout;
    }

    public void setOrderTypeLayout(OrderTypeLayout orderTypeLayout) {
        this.orderTypeLayout = orderTypeLayout;
    }

    public SelfTradeBehaviorLayout getSelfTradeBehaviorLayout() {
        return selfTradeBehaviorLayout;
    }

    public void setSelfTradeBehaviorLayout(SelfTradeBehaviorLayout selfTradeBehaviorLayout) {
        this.selfTradeBehaviorLayout = selfTradeBehaviorLayout;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    @Override
    public String toString() {
        return "Order{" +
                "price=" + price +
                ", quantity=" + quantity +
                ", clientOrderId=" + clientOrderId +
                ", floatPrice=" + floatPrice +
                ", floatQuantity=" + floatQuantity +
                ", owner=" + owner +
                ", maxQuoteQuantity=" + maxQuoteQuantity +
                ", clientId=" + clientId +
                ", orderTypeLayout=" + orderTypeLayout +
                ", selfTradeBehaviorLayout=" + selfTradeBehaviorLayout +
                ", buy=" + buy +
                '}';
    }
}
