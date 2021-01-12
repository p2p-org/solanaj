package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Decodes an Orderbook object from bytes.
 *
 * buffer_layout_1.blob(5),
 *     layout_1.accountFlagsLayout('accountFlags'),
 *     slab_1.SLAB_LAYOUT.replicate('slab'),
 *     buffer_layout_1.blob(7),
 *
 *
 *
 */
public class OrderBook {

    private AccountFlags accountFlags;
    private PublicKey ownAddress;
    private long vaultSignerNonce;
    private PublicKey baseMint;
    private PublicKey quoteMint;
    private PublicKey baseVault;
    private long baseDepositsTotal;
    private long baseFeesAccrued;
    private PublicKey quoteVault;
    private long quoteDepositsTotal;
    private long quoteFeesAccrued;
    private long quoteDustThreshold;

    private PublicKey requestQueue;
    private PublicKey eventQueue;

    private PublicKey bids;
    private PublicKey asks;

    private long baseLotSize;
    private long quoteLotSize;
    private long feeRateBps;
    private long referrerRebatesAccrued;

    private Slab slab;

    public static OrderBook readOrderBook(byte[] data) {
        final OrderBook orderBook = new OrderBook();

        final AccountFlags accountFlags = AccountFlags.readAccountFlags(data);
        orderBook.setAccountFlags(accountFlags);

        final Slab slab = Slab.readOrderBookSlab(data);
        orderBook.setSlab(slab);

        //System.out.println("bumpIndex = " + slab.getBumpIndex());

//        Path path = Paths.get("orderbook3.dat");
//        try {
//            Files.write(path, data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return orderBook;

    }

    public Slab getSlab() {
        return slab;
    }

    public void setSlab(Slab slab) {
        this.slab = slab;
    }

    public AccountFlags getAccountFlags() {
        return accountFlags;
    }

    public void setAccountFlags(AccountFlags accountFlags) {
        this.accountFlags = accountFlags;
    }

    public PublicKey getOwnAddress() {
        return ownAddress;
    }

    public void setOwnAddress(PublicKey ownAddress) {
        this.ownAddress = ownAddress;
    }

    public long getVaultSignerNonce() {
        return vaultSignerNonce;
    }

    public void setVaultSignerNonce(long vaultSignerNonce) {
        this.vaultSignerNonce = vaultSignerNonce;
    }

    public PublicKey getBaseMint() {
        return baseMint;
    }

    public void setBaseMint(PublicKey baseMint) {
        this.baseMint = baseMint;
    }

    public PublicKey getQuoteMint() {
        return quoteMint;
    }

    public void setQuoteMint(PublicKey quoteMint) {
        this.quoteMint = quoteMint;
    }

    public PublicKey getBaseVault() {
        return baseVault;
    }

    public void setBaseVault(PublicKey baseVault) {
        this.baseVault = baseVault;
    }

    public long getBaseDepositsTotal() {
        return baseDepositsTotal;
    }

    public void setBaseDepositsTotal(long baseDepositsTotal) {
        this.baseDepositsTotal = baseDepositsTotal;
    }

    public long getBaseFeesAccrued() {
        return baseFeesAccrued;
    }

    public void setBaseFeesAccrued(long baseFeesAccrued) {
        this.baseFeesAccrued = baseFeesAccrued;
    }

    public PublicKey getQuoteVault() {
        return quoteVault;
    }

    public void setQuoteVault(PublicKey quoteVault) {
        this.quoteVault = quoteVault;
    }

    public long getQuoteDepositsTotal() {
        return quoteDepositsTotal;
    }

    public void setQuoteDepositsTotal(long quoteDepositsTotal) {
        this.quoteDepositsTotal = quoteDepositsTotal;
    }

    public long getQuoteFeesAccrued() {
        return quoteFeesAccrued;
    }

    public void setQuoteFeesAccrued(long quoteFeesAccrued) {
        this.quoteFeesAccrued = quoteFeesAccrued;
    }

    public long getQuoteDustThreshold() {
        return quoteDustThreshold;
    }

    public void setQuoteDustThreshold(long quoteDustThreshold) {
        this.quoteDustThreshold = quoteDustThreshold;
    }

    public PublicKey getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(PublicKey requestQueue) {
        this.requestQueue = requestQueue;
    }

    public PublicKey getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(PublicKey eventQueue) {
        this.eventQueue = eventQueue;
    }

    public PublicKey getBids() {
        return bids;
    }

    public void setBids(PublicKey bids) {
        this.bids = bids;
    }

    public PublicKey getAsks() {
        return asks;
    }

    public void setAsks(PublicKey asks) {
        this.asks = asks;
    }

    public long getBaseLotSize() {
        return baseLotSize;
    }

    public void setBaseLotSize(long baseLotSize) {
        this.baseLotSize = baseLotSize;
    }

    public long getQuoteLotSize() {
        return quoteLotSize;
    }

    public void setQuoteLotSize(long quoteLotSize) {
        this.quoteLotSize = quoteLotSize;
    }

    public long getFeeRateBps() {
        return feeRateBps;
    }

    public void setFeeRateBps(long feeRateBps) {
        this.feeRateBps = feeRateBps;
    }

    public long getReferrerRebatesAccrued() {
        return referrerRebatesAccrued;
    }

    public void setReferrerRebatesAccrued(long referrerRebatesAccrued) {
        this.referrerRebatesAccrued = referrerRebatesAccrued;
    }

}
