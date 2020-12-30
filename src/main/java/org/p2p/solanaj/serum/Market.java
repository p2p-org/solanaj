package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

public class Market {

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


    public static Market readMarket(byte[] data) {
        Market market = new Market();

        final AccountFlags accountFlags = AccountFlags.readAccountFlags(data);
        market.setAccountFlags(accountFlags);

        final PublicKey ownAddress = SerumUtils.readOwnAddressPubkey(data);
        market.setOwnAddress(ownAddress);

        final long vaultSignerNonce = SerumUtils.readVaultSignerNonce(data);
        market.setVaultSignerNonce(vaultSignerNonce);

        final PublicKey baseMint = SerumUtils.readBaseMintPubkey(data);
        market.setBaseMint(baseMint);

        final PublicKey quoteMint = SerumUtils.readQuoteMintPubkey(data);
        market.setQuoteMint(quoteMint);

        final PublicKey baseVault = SerumUtils.readBaseVaultPubkey(data);
        market.setBaseVault(baseVault);

        final long baseDepositsTotal = SerumUtils.readBaseDepositsTotal(data);
        market.setBaseDepositsTotal(baseDepositsTotal);

        final long baseFeesAccrued = SerumUtils.readBaseFeesAccrued(data);
        market.setBaseFeesAccrued(baseFeesAccrued);

        final PublicKey quoteVault = SerumUtils.readQuoteVaultOffset(data);
        market.setQuoteVault(quoteVault);

        final long quoteDepositsTotal = SerumUtils.readQuoteDepositsTotal(data);
        market.setQuoteDepositsTotal(quoteDepositsTotal);

        final long quoteFeesAccrued = SerumUtils.readQuoteFeesAccrued(data);
        market.setQuoteFeesAccrued(quoteFeesAccrued);

        final long quoteDustThreshold = SerumUtils.readQuoteDustThreshold(data);
        market.setQuoteDustThreshold(quoteDustThreshold);

        final PublicKey requestQueue = SerumUtils.readRequestQueuePubkey(data);
        market.setRequestQueue(requestQueue);

        final PublicKey eventQueue = SerumUtils.readEventQueuePubkey(data);
        market.setEventQueue(eventQueue);

        final PublicKey bids = SerumUtils.readBidsPubkey(data);
        market.setBids(bids);

        final PublicKey asks = SerumUtils.readAsksPubkey(data);
        market.setAsks(asks);

        final long baseLotSize = SerumUtils.readBaseLotSize(data);
        market.setBaseLotSize(baseLotSize);

        final long quoteLotSize = SerumUtils.readQuoteLotSize(data);
        market.setQuoteLotSize(quoteLotSize);

        final long feeRateBps = SerumUtils.readFeeRateBps(data);
        market.setFeeRateBps(feeRateBps);

        final long referrerRebatesAccrued = SerumUtils.readReferrerRebatesAccrued(data);
        market.setReferrerRebatesAccrued(referrerRebatesAccrued);

        // temporary, for debugging
        System.out.println("Own Address = " + market.getOwnAddress().toBase58());
        System.out.println("Vault signer nonce = " + market.getVaultSignerNonce());
        System.out.println("Base mint = " + market.getBaseMint().toBase58());
        System.out.println("Quote mint = " + market.getQuoteMint().toBase58());
        System.out.println("Base vault = " + market.getBaseVault().toBase58());
        System.out.println("Base deposits total = " + market.getBaseDepositsTotal());
        System.out.println("Base fees accrued = " + market.getBaseFeesAccrued());
        System.out.println("Quote vault = " + market.getQuoteVault().toBase58());
        System.out.println("Quote deposits total = " + market.getQuoteDepositsTotal());
        System.out.println("Quote fees accrued = " + market.getQuoteFeesAccrued());
        System.out.println("Quote dust threshold = " + market.getQuoteDustThreshold());
        System.out.println("Request queue = " + market.getRequestQueue().toBase58());
        System.out.println("Event queue = " + market.getEventQueue().toBase58());
        System.out.println("Bids = " + market.getBids().toBase58());
        System.out.println("Asks = " + market.getAsks().toBase58());
        System.out.println("Base lot size = " + market.getBaseLotSize());
        System.out.println("Quote lot size = " + market.getQuoteLotSize());
        System.out.println("Fee rate bps = " + market.getFeeRateBps());
        System.out.println("Referrer rebates accrued = " + market.getReferrerRebatesAccrued());

        return market;
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
