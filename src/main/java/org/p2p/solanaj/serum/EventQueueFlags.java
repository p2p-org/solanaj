package org.p2p.solanaj.serum;

/**
 * Used to represent flags for Event Queue events
 */
public class EventQueueFlags {

    private boolean fill;
    private boolean out;
    private boolean bid;
    private boolean maker;

    public EventQueueFlags(boolean fill, boolean out, boolean bid, boolean maker) {
        this.fill = fill;
        this.out = out;
        this.bid = bid;
        this.maker = maker;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isOut() {
        return out;
    }

    public boolean isBid() {
        return bid;
    }

    public boolean isMaker() {
        return maker;
    }

    @Override
    public String toString() {
        return "EventQueueFlags{" +
                "fill=" + fill +
                ", out=" + out +
                ", bid=" + bid +
                ", maker=" + maker +
                '}';
    }
}
