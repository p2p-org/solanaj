package org.p2p.solanaj.rpc.types.config;

public enum Commitment {

    FINALIZED("finalized"),
    CONFIRMED("confirmed"),
    PROCESSED("processed"),
    SINGLE_GOSSIP("singleGossip"),
    SINGLE("single"),
    ROOT("root"),
    RECENT("recent"),
    MAX("max");

    private final String value;

    Commitment(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
