package org.p2p.solanaj.serum;

import org.bitcoinj.core.Utils;
import org.p2p.solanaj.core.PublicKey;

import java.util.ArrayList;
import java.util.List;

public class OpenOrdersAccount {

    private static final int MARKET_OFFSET = 13;
    private static final int OWNER_OFFSET = MARKET_OFFSET + 32;
    private static final int BASE_TOKEN_FREE_OFFSET = OWNER_OFFSET + 32;
    private static final int BASE_TOKEN_TOTAL_OFFSET = BASE_TOKEN_FREE_OFFSET + 8;
    private static final int QUOTE_TOKEN_FREE_OFFSET = BASE_TOKEN_TOTAL_OFFSET + 8;
    private static final int QUOTE_TOKEN_TOTAL_OFFSET = QUOTE_TOKEN_FREE_OFFSET + 8;

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

    public OpenOrdersAccount() {
        this.orders = new ArrayList<>(128);
        this.clientIds = new ArrayList<>(128);
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
}
