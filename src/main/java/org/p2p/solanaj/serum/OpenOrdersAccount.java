package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class OpenOrdersAccount {

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
    private List<byte[]> orders;
    private List<byte[]> clientIds;
    private long referrerRebatesAccrued;

    // set manually
    private PublicKey ownPubkey;

    // deserialized
    private List<Long> longPrices;
    private List<Long> orderIds;
    private List<byte[]> clientOrderIds;

    public OpenOrdersAccount() {
        this.orders = new ArrayList<>(128);
        this.clientIds = new ArrayList<>(128);
        this.longPrices = new ArrayList<>(128);
        this.clientOrderIds = new ArrayList<>(128);
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

        long firstClientId = Utils.readInt64(clientIds, 0);
        long secondClientId = Utils.readInt64(clientIds, 8);
        long thirdClientId = Utils.readInt64(clientIds, 16);
        long fourthClientId = Utils.readInt64(clientIds, 24);

        final List<Long> orderIds = new ArrayList<>();
        final List<Long> prices = new ArrayList<>();
        // ?
        final List<byte[]> clientOrderIds = new ArrayList<>();

        for (int i = 0; i < 128; i++) {
            // read clientId
            orderIds.add(Utils.readInt64(clientIds, i * 8));
            clientOrderIds.add(Arrays.copyOfRange(orders, i * 16, (i * 16) + 16));

            // read price
            prices.add(Utils.readInt64(orders, (i * 16) + 8));
        }

        openOrdersAccount.setOrderIds(orderIds);
        openOrdersAccount.setLongPrices(prices);
        openOrdersAccount.setClientOrderIds(clientOrderIds);

        Logger.getAnonymousLogger().info(String.format("Order IDs: %d, %d, %d, %d", firstClientId, secondClientId, thirdClientId, fourthClientId));

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

    public List<byte[]> getOrders() {
        return orders;
    }

    public void setOrders(List<byte[]> orders) {
        this.orders = orders;
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
}
