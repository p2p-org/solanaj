package org.p2p.solanaj.rpc.types;

import lombok.Getter;

import java.util.AbstractMap;

@Getter
public class PerformanceSample {

    private final double slot;
    private final double numTransactions;
    private final double numSlots;
    private final double samplePeriodsSecs;

    @SuppressWarnings("rawtypes")
    public PerformanceSample(AbstractMap am) {
        this.slot = (double) am.get("slot");
        this.numTransactions = (double) am.get("numTransactions");
        this.numSlots = (double) am.get("numSlots");
        this.samplePeriodsSecs = (double) am.get("samplePeriodSecs");
    }
}
