package org.p2p.solanaj.serum;

/**
 * Enum representing an {@link Order}s order type such as limit, ioc, or post only.
 */
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
