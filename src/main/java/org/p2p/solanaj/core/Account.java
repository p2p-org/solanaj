package org.p2p.solanaj.core;

import org.p2p.solanaj.utils.TweetNaclFast;

public class Account {
    private TweetNaclFast.Signature.KeyPair keyPair;

    public Account(byte[] secretKey) {
        this.keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(secretKey);
    }

    public String getPublicKey() {
        return new PublicKey(keyPair.getPublicKey()).toString();
    }

    public byte[] getSecretKey() {
        return keyPair.getSecretKey();
    }
}
