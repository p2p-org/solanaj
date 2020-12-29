package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.util.Arrays;

public class Market {

    private AccountFlags accountFlags;
    private PublicKey ownAddress;

    public static Market readMarket(byte[] data) {
        Market market = new Market();

        // Account flags
        final AccountFlags accountFlags = AccountFlags.readAccountFlags(data);
        market.setAccountFlags(accountFlags);

        // publicKeyLayout("ownAddress") = 32 bytes from position 13 (8 bytes for the WideBit starting at position 5)
        final PublicKey ownAddress = PublicKey.readSerumPubkey(data);
        market.setOwnAddress(ownAddress);

        // temporary, for debugging
        System.out.println("Account Flags (deserialized) (Initialized flag) = " + accountFlags.isInitialized());
        System.out.println("Own Address (Base58) = " + ownAddress.toBase58());
        System.out.println("Blob #2 (last 7 bytes) = " + new String(Arrays.copyOfRange(data, data.length - 7, data.length)));

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
}
