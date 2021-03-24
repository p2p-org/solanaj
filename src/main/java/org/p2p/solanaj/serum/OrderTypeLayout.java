package org.p2p.solanaj.serum;

public enum OrderTypeLayout {
    LIMIT(0),
    IOC(1),
    POST_ONLY(2);

    private final int value;

    OrderTypeLayout(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
