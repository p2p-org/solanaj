package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class EpochSchedule {

    @Json(name = "slotsPerEpoch")
    private long slotsPerEpoch;

    @Json(name = "leaderScheduleSlotOffset")
    private long leaderScheduleSlotOffset;

    @Json(name = "warmup")
    private boolean warmup;

    @Json(name = "firstNormalEpoch")
    private long firstNormalEpoch;

    @Json(name = "firstNormalSlot")
    private long firstNormalSlot;

    public long getSlotsPerEpoch() {
        return slotsPerEpoch;
    }

    public void setSlotsPerEpoch(long slotsPerEpoch) {
        this.slotsPerEpoch = slotsPerEpoch;
    }

    public long getLeaderScheduleSlotOffset() {
        return leaderScheduleSlotOffset;
    }

    public void setLeaderScheduleSlotOffset(long leaderScheduleSlotOffset) {
        this.leaderScheduleSlotOffset = leaderScheduleSlotOffset;
    }

    public boolean getWarmup() {
        return warmup;
    }

    public void setWarmup(boolean warmup) {
        this.warmup = warmup;
    }

    public long getFirstNormalEpoch() {
        return firstNormalEpoch;
    }

    public void setFirstNormalEpoch(long firstNormalEpoch) {
        this.firstNormalEpoch = firstNormalEpoch;
    }

    public long getFirstNormalSlot() {
        return firstNormalSlot;
    }

    public void setFirstNormalSlot(long firstNormalSlot) {
        this.firstNormalSlot = firstNormalSlot;
    }

    public EpochSchedule() {
    }

    public EpochSchedule(long slotsPerEpoch, long leaderScheduleSlotOffset, boolean warmup, long firstNormalEpoch, long firstNormalSlot) {
        this.slotsPerEpoch = slotsPerEpoch;
        this.leaderScheduleSlotOffset = leaderScheduleSlotOffset;
        this.warmup = warmup;
        this.firstNormalEpoch = firstNormalEpoch;
        this.firstNormalSlot = firstNormalSlot;
    }

    @Override
    public String toString() {
        return "EpochSchedule{" +
                "slotsPerEpoch=" + slotsPerEpoch +
                ", leaderScheduleSlotOffset=" + leaderScheduleSlotOffset +
                ", warmup=" + warmup +
                ", firstNormalEpoch=" + firstNormalEpoch +
                ", firstNormalSlot=" + firstNormalSlot +
                '}';
    }
}
