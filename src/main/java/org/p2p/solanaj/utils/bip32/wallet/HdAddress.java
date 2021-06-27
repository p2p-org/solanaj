package org.p2p.solanaj.utils.bip32.wallet;


import org.p2p.solanaj.utils.bip32.wallet.key.HdPrivateKey;
import org.p2p.solanaj.utils.bip32.wallet.key.HdPublicKey;

/**
 * An HD pub/private key
 */
public class HdAddress {

    private final HdPrivateKey privateKey;
    private final HdPublicKey publicKey;
    private final SolanaCoin solanaCoin;
    private final String path;

    public HdAddress(HdPrivateKey privateKey, HdPublicKey publicKey, SolanaCoin solanaCoin, String path) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.solanaCoin = solanaCoin;
        this.path = path;
    }

    public HdPrivateKey getPrivateKey() {
        return privateKey;
    }

    public HdPublicKey getPublicKey() {
        return publicKey;
    }

    public SolanaCoin getCoinType() {
        return solanaCoin;
    }

    public String getPath() {
        return path;
    }
}
