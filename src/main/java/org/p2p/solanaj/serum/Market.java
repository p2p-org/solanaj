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

        // temporary, for debugging
        System.out.println("Own Address (Base58) = " + market.getOwnAddress().toBase58());
        System.out.println("Vault signer nonce = " + vaultSignerNonce);
        System.out.println("Base mint (Base58) = " + market.getBaseMint().toBase58());
        System.out.println("Quote mint (Base58) = " + market.getQuoteMint().toBase58());
        System.out.println("Base vault (Base58) = " + market.getBaseVault().toBase58());
        System.out.println("Base deposits total = " + market.getBaseDepositsTotal());

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
}
