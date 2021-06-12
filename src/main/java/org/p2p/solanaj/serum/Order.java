package org.p2p.solanaj.serum;

import lombok.*;
import org.p2p.solanaj.core.PublicKey;

/**
 * Class that represents a Serum order.
 */
@Builder
@Getter
@Setter(AccessLevel.PACKAGE)
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
