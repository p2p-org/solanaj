package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class EpochInfo {

    @Json(name = "absoluteSlot")
    private long absoluteSlot;

    @Json(name = "blockHeight")
    private long blockHeight;

    @Json(name = "epoch")
    private long epoch;

    @Json(name = "slotIndex")
    private long slotIndex;

    @Json(name = "slotsInEpoch")
    private long slotsInEpoch;

    public EpochInfo() {
    }

    public EpochInfo(long absoluteSlot, long blockHeight, long epoch, long slotIndex, long slotsInEpoch) {
        this.absoluteSlot = absoluteSlot;
        this.blockHeight = blockHeight;
        this.epoch = epoch;
        this.slotIndex = slotIndex;
        this.slotsInEpoch = slotsInEpoch;
    }

    public long getAbsoluteSlot() {
        return absoluteSlot;
    }

    public void setAbsoluteSlot(long absoluteSlot) {
        this.absoluteSlot = absoluteSlot;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public long getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(long slotIndex) {
        this.slotIndex = slotIndex;
    }

    public long getSlotsInEpoch() {
        return slotsInEpoch;
    }

    public void setSlotsInEpoch(long slotsInEpoch) {
        this.slotsInEpoch = slotsInEpoch;
    }

    @Override
    public String toString() {
        return "EpochInfo{" +
                "absoluteSlot=" + absoluteSlot +
                ", blockHeight=" + blockHeight +
                ", epoch=" + epoch +
                ", slotIndex=" + slotIndex +
                ", slotsInEpoch=" + slotsInEpoch +
                '}';
    }
}
    