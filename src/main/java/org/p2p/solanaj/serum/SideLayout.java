package org.p2p.solanaj.serum;

public enum SideLayout {
    BUY(0),
    SELL(1);

    private final int value;

    SideLayout(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
