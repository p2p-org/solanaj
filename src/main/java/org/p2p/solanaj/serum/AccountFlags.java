package org.p2p.solanaj.serum;

import java.util.Arrays;

public class AccountFlags {

    // Holds all 8 booleans (1 for each bit)
    private final byte bitMask;

    private final int initialized = 1;  // Binary 00000001
    private final int market = 2;  // Binary 00000010
    private final int openOrders = 4;  // Binary 00000100
    private final int requestQueue = 8;  // Binary 00001000
    private final int eventQueue = 16;  // Binary 00010000
    private final int bids = 32;  // Binary 00100000
    private final int asks = 64;  // Binary 01000000

    public AccountFlags(byte bitMask) {
        this.bitMask = bitMask;
    }

    public boolean isInitialized() {
        return ((bitMask & initialized) == initialized);
    }

    public boolean isMarket() {
        return ((bitMask & market) == market);
    }

    public boolean isOpenOrders() {
        return ((bitMask & openOrders) == openOrders);
    }

    public boolean isRequestQueue() {
        return ((bitMask & requestQueue) == requestQueue);
    }

    public boolean isEventQueue() {
        return ((bitMask & eventQueue) == eventQueue);
    }

    public boolean isBids() {
        return ((bitMask & bids) == bids);
    }

    public boolean isAsks() {
        return ((bitMask & asks) == asks);
    }

    public static AccountFlags readAccountFlags(byte[] data) {
        return new AccountFlags(Arrays.copyOfRange(data, 5, 6)[0]);
    }


}
