package org.p2p.solanaj.core;

import org.p2p.solanaj.utils.TweetNaclFast;

public class Account {
    private TweetNaclFast.Signature.KeyPair keyPair;

    public Account() {
        this.keyPair = TweetNaclFast.Signature.keyPair();
    }

    public Account(byte[] secretKey) {
        this.keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(secretKey);
    }

    public PublicKey getPublicKey() {
        return new PublicKey(keyPair.getPublicKey());
    }

    public byte[] getSecretKey() {
        return keyPair.getSecretKey();
    }
}
