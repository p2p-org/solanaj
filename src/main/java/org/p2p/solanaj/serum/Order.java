package org.p2p.solanaj.serum;

/**
 * Class that represents a Serum order.
 */
public class Order {

    private long price;
    private long quantity;
    private long clientOrderId;

    public Order(long price, long quantity, long clientOrderId) {
        this.price = price;
        this.quantity = quantity;
        this.clientOrderId = clientOrderId;
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

    @Override
    public String toString() {
        return "Order{" +
                "price=" + price +
                ", quantity=" + quantity +
                ", clientOrderId=" + clientOrderId +
                '}';
    }
}
