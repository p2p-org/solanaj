package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class InflationGovernor {

    @Json(name = "initial")
    private float initial;

    @Json(name = "terminal")
    private float terminal;

    @Json(name = "taper")
    private float taper;

    @Json(name = "foundation")
    private float foundation;

    @Json(name = "foundationTerm")
    private long foundationTerm;

    public InflationGovernor() {
    }

    public InflationGovernor(float initial, float terminal, float taper, float foundation, long foundationTerm) {
        this.initial = initial;
        this.terminal = terminal;
        this.taper = taper;
        this.foundation = foundation;
        this.foundationTerm = foundationTerm;
    }

    public float getInitial() {
        return initial;
    }

    public void setInitial(float initial) {
        this.initial = initial;
    }

    public float getTerminal() {
        return terminal;
    }

    public void setTerminal(float terminal) {
        this.terminal = terminal;
    }

    public float getTaper() {
        return taper;
    }

    public void setTaper(float taper) {
        this.taper = taper;
    }

    public float getFoundation() {
        return foundation;
    }

    public void setFoundation(float foundation) {
        this.foundation = foundation;
    }

    public long getFoundationTerm() {
        return foundationTerm;
    }

    public void setFoundationTerm(long foundationTerm) {
        this.foundationTerm = foundationTerm;
    }

    @Override
    public String toString() {
        return "InflationGovernor{" +
                "initial=" + initial +
                ", terminal=" + terminal +
                ", taper=" + taper +
                ", foundation=" + foundation +
                ", foundationTerm=" + foundationTerm +
                '}';
    }
}
