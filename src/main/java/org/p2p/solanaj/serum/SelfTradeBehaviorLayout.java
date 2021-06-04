package org.p2p.solanaj.serum;

/**
 * Enum representing how your Serum {@link Order} behaves when you trade against yourself
 */
public enum SelfTradeBehaviorLayout {
    DECREMENT_TAKE(0),
    CANCEL_PROVIDE(1),
    ABORT_TRANSACTION(2);

    private final int value;

    SelfTradeBehaviorLayout(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
