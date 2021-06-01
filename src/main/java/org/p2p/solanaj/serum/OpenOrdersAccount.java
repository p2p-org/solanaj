package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class OpenOrdersAccount {

    public static class Order {
        private int orderIndex;
        private long clientId;
        private byte[] clientOrderId;
        private long price;
        private boolean isFreeSlot;
        private boolean isBid;
        private float floatPrice;

        public int getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public long getClientId() {
            return clientId;
        }

        public void setClientId(long clientId) {
            this.clientId = clientId;
        }

        public byte[] getClientOrderId() {
            return clientOrderId;
        }

        public void setClientOrderId(byte[] clientOrderId) {
            this.clientOrderId = clientOrderId;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public boolean isFreeSlot() {
            return isFreeSlot;
        }

        public void setFreeSlot(boolean freeSlot) {
            isFreeSlot = freeSlot;
        }

        public boolean isBid() {
            return isBid;
        }

        public void setBid(boolean bid) {
            isBid = bid;
        }

        public float getFloatPrice() {
            return floatPrice;
        }

        public void setFloatPrice(float floatPrice) {
            this.floatPrice = floatPrice;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "orderIndex=" + orderIndex +
                    ", clientId=" + clientId +
                    ", clientOrderId=" + Arrays.toString(clientOrderId) +
                    ", price=" + price +
                    ", isFreeSlot=" + isFreeSlot +
                    ", isBid=" + isBid +
                    ", floatPrice=" + floatPrice +
                    '}';
        }
    }

    private static final int MARKET_OFFSET = 13;
    private static final int OWNER_OFFSET = MARKET_OFFSET + 32;
    private static final int BASE_TOKEN_FREE_OFFSET = OWNER_OFFSET + 32;
    private static final int BASE_TOKEN_TOTAL_OFFSET = BASE_TOKEN_FREE_OFFSET + 8;
    private static final int QUOTE_TOKEN_FREE_OFFSET = BASE_TOKEN_TOTAL_OFFSET + 8;
    private static final int QUOTE_TOKEN_TOTAL_OFFSET = QUOTE_TOKEN_FREE_OFFSET + 8;
    private static final int FREE_SLOT_BITS_OFFSET = QUOTE_TOKEN_TOTAL_OFFSET + 8;
    private static final int IS_BID_BITS_OFFSET = FREE_SLOT_BITS_OFFSET + 16;
    private static final int ORDERS_OFFSET = IS_BID_BITS_OFFSET + 16;
    private static final int CLIENT_IDS_OFFSET = ORDERS_OFFSET + 2048;

    private AccountFlags accountFlags;
    private PublicKey market;
    private PublicKey owner;
    private long baseTokenFree;
    private long baseTokenTotal;
    private long quoteTokenFree;
    private long quoteTokenTotal;
    private byte[] freeSlotBits;
    private byte[] isBidBits;
    private List<byte[]> clientIds;
    private long referrerRebatesAccrued;

    // set manually
    private PublicKey ownPubkey;

    // deserialized
    private List<Long> longPrices;
    private List<Long> orderIds;
    private List<byte[]> clientOrderIds;
    private List<Boolean> freeSlots; // true if the index is free
    private List<Boolean> bidSlots; // true if the order is a bid
    private List<OpenOrdersAccount.Order> orders;

    public OpenOrdersAccount() {
        this.clientIds = new ArrayList<>(128);
        this.longPrices = new ArrayList<>(128);
        this.clientOrderIds = new ArrayList<>(128);
        this.freeSlots = new ArrayList<>(128);
        this.bidSlots = new ArrayList<>(128);
        this.orders = new ArrayList<>(128);
    }

    public static OpenOrdersAccount readOpenOrdersAccount(byte[] data) {
        OpenOrdersAccount openOrdersAccount = new OpenOrdersAccount();

        final AccountFlags accountFlags = AccountFlags.readAccountFlags(data);
        openOrdersAccount.setAccountFlags(accountFlags);

        final PublicKey marketPubkey = PublicKey.readPubkey(data, MARKET_OFFSET);
        openOrdersAccount.setMarket(marketPubkey);

        final PublicKey ownerPubkey = PublicKey.readPubkey(data, OWNER_OFFSET);
        openOrdersAccount.setOwner(ownerPubkey);

        // baseTokenFree = unsettled balance
        final long baseTokenFree = Utils.readInt64(data, BASE_TOKEN_FREE_OFFSET);
        openOrdersAccount.setBaseTokenFree(baseTokenFree);

        final long baseTokenTotal = Utils.readInt64(data, BASE_TOKEN_TOTAL_OFFSET);
        openOrdersAccount.setBaseTokenTotal(baseTokenTotal);

        final long quoteTokenFree = Utils.readInt64(data, QUOTE_TOKEN_FREE_OFFSET);
        openOrdersAccount.setQuoteTokenFree(quoteTokenFree);

        final long quoteTokenTotal = Utils.readInt64(data, QUOTE_TOKEN_TOTAL_OFFSET);
        openOrdersAccount.setQuoteTokenTotal(quoteTokenTotal);

        byte[] freeSlotBits = Arrays.copyOfRange(data, FREE_SLOT_BITS_OFFSET, IS_BID_BITS_OFFSET);
        byte[] isBidBits = Arrays.copyOfRange(data, IS_BID_BITS_OFFSET, ORDERS_OFFSET);
        openOrdersAccount.setFreeSlotBits(freeSlotBits);
        openOrdersAccount.setIsBidBits(isBidBits);

        // orders = 128 * 16 = 2048 bytes of orders

        byte[] orders = Arrays.copyOfRange(data, ORDERS_OFFSET, CLIENT_IDS_OFFSET);
        byte[] clientIds = Arrays.copyOfRange(data, CLIENT_IDS_OFFSET, CLIENT_IDS_OFFSET + 1024);

        final List<Long> orderIds = new ArrayList<>();
        final List<Long> prices = new ArrayList<>();
        // ?
        final List<byte[]> clientOrderIds = new ArrayList<>();
        final List<Boolean> freeSlots = new ArrayList<>();
        final List<Boolean> bidSlots = new ArrayList<>();
        final List<OpenOrdersAccount.Order> openOrdersAccountOrders = new ArrayList<>();

        for (int i = 0; i < 128; i++) {
            // read clientId
            long clientId = Utils.readInt64(clientIds, i * 8);
            byte[] clientOrderId = Arrays.copyOfRange(orders, i * 16, (i * 16) + 16);

            orderIds.add(clientId);
            clientOrderIds.add(clientOrderId);

            // read price
            long price = Utils.readInt64(orders, (i * 16) + 8);
            boolean isFreeSlot = ByteUtils.getBit(freeSlotBits, i) == 1;
            boolean isBid = ByteUtils.getBit(isBidBits, i) == 1;

            prices.add(price);
            freeSlots.add(isFreeSlot);
            bidSlots.add(isBid);

            if (!isFreeSlot) {
                OpenOrdersAccount.Order order = new OpenOrdersAccount.Order();
                order.setOrderIndex(i);
                order.setBid(isBid);
                order.setClientOrderId(clientOrderId);
                order.setFreeSlot(isFreeSlot);
                order.setPrice(price);
                order.setClientId(clientId);
                openOrdersAccountOrders.add(order);
            }
        }

        openOrdersAccount.setOrders(openOrdersAccountOrders);
        openOrdersAccount.setOrderIds(orderIds);
        openOrdersAccount.setLongPrices(prices);
        openOrdersAccount.setClientOrderIds(clientOrderIds);
        openOrdersAccount.setFreeSlots(freeSlots);
        openOrdersAccount.setBidSlots(bidSlots);

        Logger.getAnonymousLogger().info(
                String.format(
                        "Order IDs: %d, %d",
                        Utils.readInt64(clientIds, 0),
                        Utils.readInt64(clientIds, 8)
                )
        );

        return openOrdersAccount;
    }

    public AccountFlags getAccountFlags() {
        return accountFlags;
    }

    public void setAccountFlags(AccountFlags accountFlags) {
        this.accountFlags = accountFlags;
    }

    public PublicKey getMarket() {
        return market;
    }

    public void setMarket(PublicKey market) {
        this.market = market;
    }

    public PublicKey getOwner() {
        return owner;
    }

    public void setOwner(PublicKey owner) {
        this.owner = owner;
    }

    public long getBaseTokenFree() {
        return baseTokenFree;
    }

    public void setBaseTokenFree(long baseTokenFree) {
        this.baseTokenFree = baseTokenFree;
    }

    public long getBaseTokenTotal() {
        return baseTokenTotal;
    }

    public void setBaseTokenTotal(long baseTokenTotal) {
        this.baseTokenTotal = baseTokenTotal;
    }

    public long getQuoteTokenFree() {
        return quoteTokenFree;
    }

    public void setQuoteTokenFree(long quoteTokenFree) {
        this.quoteTokenFree = quoteTokenFree;
    }

    public long getQuoteTokenTotal() {
        return quoteTokenTotal;
    }

    public void setQuoteTokenTotal(long quoteTokenTotal) {
        this.quoteTokenTotal = quoteTokenTotal;
    }

    public byte[] getFreeSlotBits() {
        return freeSlotBits;
    }

    public void setFreeSlotBits(byte[] freeSlotBits) {
        this.freeSlotBits = freeSlotBits;
    }

    public byte[] getIsBidBits() {
        return isBidBits;
    }

    public void setIsBidBits(byte[] isBidBits) {
        this.isBidBits = isBidBits;
    }

    public List<byte[]> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<byte[]> clientIds) {
        this.clientIds = clientIds;
    }

    public long getReferrerRebatesAccrued() {
        return referrerRebatesAccrued;
    }

    public void setReferrerRebatesAccrued(long referrerRebatesAccrued) {
        this.referrerRebatesAccrued = referrerRebatesAccrued;
    }

    public PublicKey getOwnPubkey() {
        return ownPubkey;
    }

    public void setOwnPubkey(PublicKey ownPubkey) {
        this.ownPubkey = ownPubkey;
    }

    public List<Long> getLongPrices() {
        return longPrices;
    }

    public void setLongPrices(List<Long> longPrices) {
        this.longPrices = longPrices;
    }

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public List<byte[]> getClientOrderIds() {
        return clientOrderIds;
    }

    public void setClientOrderIds(List<byte[]> clientOrderIds) {
        this.clientOrderIds = clientOrderIds;
    }

    public List<Boolean> getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(List<Boolean> freeSlots) {
        this.freeSlots = freeSlots;
    }

    public List<Boolean> getBidSlots() {
        return bidSlots;
    }

    public void setBidSlots(List<Boolean> bidSlots) {
        this.bidSlots = bidSlots;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
