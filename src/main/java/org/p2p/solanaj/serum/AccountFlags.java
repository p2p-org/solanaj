package org.p2p.solanaj.serum;

import java.util.Arrays;

/**
 * Class to represent a Serum account's flags
 */
public class AccountFlags {

    // Holds all 8 booleans (1 for each bit)
    private final byte bitMask;

    private static final int INITIALIZED = 1;  // Binary 00000001
    private static final int MARKET = 2;  // Binary 00000010
    private static final int OPEN_ORDERS = 4;  // Binary 00000100
    private static final int REQUEST_QUEUE = 8;  // Binary 00001000
    private static final int EVENT_QUEUE = 16;  // Binary 00010000
    private static final int BIDS = 32;  // Binary 00100000
    private static final int ASKS = 64;  // Binary 01000000

    public AccountFlags(byte bitMask) {
        this.bitMask = bitMask;
    }

    public boolean isInitialized() {
        return ((bitMask & INITIALIZED) == INITIALIZED);
    }

    public boolean isMarket() {
        return ((bitMask & MARKET) == MARKET);
    }

    public boolean isOpenOrders() {
        return ((bitMask & OPEN_ORDERS) == OPEN_ORDERS);
    }

    public boolean isRequestQueue() {
        return ((bitMask & REQUEST_QUEUE) == REQUEST_QUEUE);
    }

    public boolean isEventQueue() {
        return ((bitMask & EVENT_QUEUE) == EVENT_QUEUE);
    }

    public boolean isBids() {
        return ((bitMask & BIDS) == BIDS);
    }

    public boolean isAsks() {
        return ((bitMask & ASKS) == ASKS);
    }

    public static AccountFlags readAccountFlags(byte[] data) {
        return new AccountFlags(Arrays.copyOfRange(data, 5, 12)[0]);
    }

    @Override
    public String toString() {
        return "AccountFlags{" +
                "bitMask=" + bitMask +
                ", initialized=" + isInitialized() +
                ", market=" + isMarket() +
                ", openOrders=" + isOpenOrders() +
                ", requestQueue=" + isRequestQueue() +
                ", eventQueue=" + isEventQueue() +
                ", bids=" + isBids() +
                ", asks=" + isAsks() +
                '}';
    }
}
