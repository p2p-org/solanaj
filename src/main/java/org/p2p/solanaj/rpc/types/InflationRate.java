package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class InflationRate {

    @Json(name = "total")
    private float total;

    @Json(name = "validator")
    private float validator;

    @Json(name = "foundation")
    private float foundation;

    @Json(name = "epoch")
    private long epoch;

    public InflationRate() {
    }

    public InflationRate(float total, float validator, float foundation, long epoch) {
        this.total = total;
        this.validator = validator;
        this.foundation = foundation;
        this.epoch = epoch;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getValidator() {
        return validator;
    }

    public void setValidator(float validator) {
        this.validator = validator;
    }

    public float getFoundation() {
        return foundation;
    }

    public void setFoundation(float foundation) {
        this.foundation = foundation;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    @Override
    public String toString() {
        return "InflationRate{" +
                "total=" + total +
                ", validator=" + validator +
                ", foundation=" + foundation +
                ", epoch=" + epoch +
                '}';
    }
}
