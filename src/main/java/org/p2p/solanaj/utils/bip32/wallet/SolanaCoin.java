package org.p2p.solanaj.utils.bip32.wallet;

import org.p2p.solanaj.utils.bip32.wallet.key.SolanaCurve;

public class SolanaCoin {
    private final SolanaCurve curve = new SolanaCurve();
    private final long coinType = 501;
    private final long purpose = 44;
    private final boolean alwaysHardened = true;

    /**
     * Get the curve
     *
     * @return curve
     */
    public SolanaCurve getCurve() {
        return curve;
    }

    /**
     * get the coin type
     *
     * @return coin type
     */
    public long getCoinType() {
        return coinType;
    }

    /**
     * get whether the addresses must always be hardened
     *
     * @return always hardened
     */
    public boolean getAlwaysHardened() {
        return alwaysHardened;
    }

    /**
     * get the coin purpose
     *
     * @return purpose
     */
    public long getPurpose() {
        return purpose;
    }
}
