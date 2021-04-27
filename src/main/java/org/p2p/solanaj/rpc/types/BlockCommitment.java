package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class BlockCommitment {

    @Json(name = "commitment")
    private long[] commitment;

    @Json(name = "totalStake")
    private long totalStake;

    public BlockCommitment() {
    }

    public BlockCommitment(long[] commitment, long totalStake) {
        this.commitment = commitment;
        this.totalStake = totalStake;
    }

    public long[] getCommitment() {
        return commitment;
    }

    public void setCommitment(long[] commitment) {
        this.commitment = commitment;
    }

    public long getTotalStake() {
        return totalStake;
    }

    public void setTotalStake(long totalStake) {
        this.totalStake = totalStake;
    }
}
