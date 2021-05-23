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

    @Override
    public String toString() {
        return "Order{" +
                "price=" + price +
                ", quantity=" + quantity +
                ", clientOrderId=" + clientOrderId +
                ", floatPrice=" + floatPrice +
                ", floatQuantity=" + floatQuantity +
                ", owner=" + owner +
                '}';
    }
}
