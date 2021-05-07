package org.p2p.solanaj.core;

import java.nio.ByteBuffer;
import java.util.Arrays;
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

    /**
     * Creates an {@link Account} object from a Sollet-exported JSON string (array)
     * @param json Sollet-exported JSON string (array)
     * @return {@link Account} built from Sollet-exported private key
     */
    public static Account fromJson(String json) {
        return new Account(convertJsonStringToByteArray(json));
    }

    public PublicKey getPublicKey() {
        return new PublicKey(keyPair.getPublicKey());
    }

    public byte[] getSecretKey() {
        return keyPair.getSecretKey();
    }

    /**
     * Convert's a Sollet-exported JSON string into a byte array usable for {@link Account} instantiation
     * @param characters Sollet-exported JSON string
     * @return byte array usable in {@link Account} instantiation
     */
    private static byte[] convertJsonStringToByteArray(String characters) {
        // Create resulting byte array
        ByteBuffer buffer = ByteBuffer.allocate(64);

        // Convert json array into String array
        String sanitizedJson = characters.replaceAll("\\[", "").replaceAll("]", "");
        String[] chars = sanitizedJson.split(",");

        // Convert each String character into byte and put it in the buffer
        Arrays.stream(chars).forEach(character -> {
            byte byteValue = (byte) Integer.parseInt(character);
            buffer.put(byteValue);
        });

        return buffer.array();
    }
}