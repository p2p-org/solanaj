package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.AbstractMap;

public class InflationReward {

    @Json(name = "epoch")
    private double epoch;

    @Json(name = "effectiveSlot")
    private double effectiveSlot;

    @Json(name = "amount")
    private double amount;

    @Json(name = "postBalance")
    private double postBalance;

    public InflationReward() {
    }

    public InflationReward(long epoch, long effectiveSlot, long amount, long postBalance) {
        this.epoch = epoch;
        this.effectiveSlot = effectiveSlot;
        this.amount = amount;
        this.postBalance = postBalance;
    }

    // Constructor for deserializing into List
    @SuppressWarnings({ "rawtypes" })
    public InflationReward(AbstractMap pa) {
        this.epoch = (Double) pa.get("epoch");
        this.effectiveSlot = (Double) pa.get("effectiveSlot");
        this.amount = (Double) pa.get("amount");
        this.postBalance = (Double) pa.get("postBalance");
    }

    public double getEpoch() {
        return epoch;
    }

    public void setEpoch(double epoch) {
        this.epoch = epoch;
    }

    public double getEffectiveSlot() {
        return effectiveSlot;
    }

    public void setEffectiveSlot(double effectiveSlot) {
        this.effectiveSlot = effectiveSlot;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPostBalance() {
        return postBalance;
    }

    public void setPostBalance(double postBalance) {
        this.postBalance = postBalance;
    }

    @Override
    public String toString() {
        return "InflationReward{" +
                "epoch=" + epoch +
                ", effectiveSlot=" + effectiveSlot +
                ", amount=" + amount +
                ", postBalance=" + postBalance +
                '}';
    }
}
