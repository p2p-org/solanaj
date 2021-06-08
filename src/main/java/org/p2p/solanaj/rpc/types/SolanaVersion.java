package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class SolanaVersion {

    @Json(name = "solana-core")
    private String solanaCore;

    @Json(name = "feature-set")
    private String featureSet;

    public SolanaVersion() {
    }

    public String getSolanaCore() {
        return solanaCore;
    }

    public String getFeatureSet() {
        return featureSet;
    }

    public void setSolanaCore(String solanaCore) {
        this.solanaCore = solanaCore;
    }

    public void setFeatureSet(String featureSet) {
        this.featureSet = featureSet;
    }
}
