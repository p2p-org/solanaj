package org.p2p.solanaj.core;

import java.util.List;

import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.p2p.solanaj.utils.TweetNaclFast;

public class Account {
    private TweetNaclFast.Signature.KeyPair keyPair;

    public Account() {
        this.keyPair = TweetNaclFast.Signature.keyPair();
    }

    public Account(byte[] secretKey) {
        this.keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(secretKey);
    }

    private Account(TweetNaclFast.Signature.KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public static Account fromMnemonic(List<String> words, String passphrase) {
        byte[] seed = MnemonicCode.toSeed(words, passphrase);
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        DeterministicKey child = deterministicHierarchy.get(HDUtils.parsePath("M/501H/0H/0/0"), true, true);

        TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSeed(child.getPrivKeyBytes());

        return new Account(keyPair);
    }

    public PublicKey getPublicKey() {
        return new PublicKey(keyPair.getPublicKey());
    }

    public byte[] getSecretKey() {
        return keyPair.getSecretKey();
    }
}
