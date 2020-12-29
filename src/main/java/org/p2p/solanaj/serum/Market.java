package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.PublicKey;

import java.util.Arrays;

public class Market {

    private AccountFlags accountFlags;

    public static Market readMarket(byte[] data) {
        Market market = new Market();
        AccountFlags tmpAccountFlags = new AccountFlags(Arrays.copyOfRange(data, 5, 6)[0]);
        System.out.println("Blob #1 (5 bytes) = " + new String(Arrays.copyOfRange(data, 0, 5)));

        System.out.println("Account Flags (deserialized) (Initialized flag) = " + tmpAccountFlags.isInitialized());

        // publicKeyLayout("ownAddress") = 32 bytes from position 13 (8 bytes for the WideBit starting at position 5)
        final PublicKey ownAddress = PublicKey.readPubkey(data, 13);
        System.out.println("Own Address (Base58) = " + ownAddress.toBase58());

        System.out.println("Blob #2 (last 7 bytes) = " + new String(Arrays.copyOfRange(data, data.length - 7, data.length)));

        return new Market();
    }
}
