package org.p2p.solanaj.utils.bip32.wallet.key;

public class SolanaCurve {
    private static final String ed25519Curve = "ed25519 seed";

    private final String seed = SolanaCurve.ed25519Curve;

    public String getSeed() {
        return seed;
    }
}
