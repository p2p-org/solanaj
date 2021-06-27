package org.p2p.solanaj.utils.bip32.wallet.key;

import org.p2p.solanaj.utils.bip32.crypto.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Marshalling code for HDKeys to base58 representations.
 * <p>
 * Will probably be migrated to builder pattern.
 */
public class HdKey {
    private byte[] version;
    private int depth;
    private byte[] fingerprint;
    private byte[] childNumber;
    private byte[] chainCode;
    private byte[] keyData;

    HdKey(byte[] version, int depth, byte[] fingerprint, byte[] childNumber, byte[] chainCode, byte[] keyData) {
        this.version = version;
        this.depth = depth;
        this.fingerprint = fingerprint;
        this.childNumber = childNumber;
        this.chainCode = chainCode;
        this.keyData = keyData;
    }

    HdKey() {
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setFingerprint(byte[] fingerprint) {
        this.fingerprint = fingerprint;
    }

    public void setChildNumber(byte[] childNumber) {
        this.childNumber = childNumber;
    }

    public void setChainCode(byte[] chainCode) {
        this.chainCode = chainCode;
    }

    public void setKeyData(byte[] keyData) {
        this.keyData = keyData;
    }

    public byte[] getChainCode() {
        return chainCode;
    }

    /**
     * Get the full chain key.  This is not the public/private key for the address.
     * @return full HD Key
     */
    public byte[] getKey() {

        ByteArrayOutputStream key = new ByteArrayOutputStream();

        try {
            key.write(version);
            key.write(new byte[]{(byte) depth});
            key.write(fingerprint);
            key.write(childNumber);
            key.write(chainCode);
            key.write(keyData);
            byte[] checksum = Hash.sha256Twice(key.toByteArray());
            key.write(Arrays.copyOfRange(checksum, 0, 4));
        } catch (IOException e) {
            throw new RuntimeException("Unable to write key");
        }

        return key.toByteArray();
    }

    public int getDepth() {
        return depth;
    }

    public byte[] getKeyData() {
        return keyData;
    }

    public byte[] getVersion() {
        return version;
    }
}
