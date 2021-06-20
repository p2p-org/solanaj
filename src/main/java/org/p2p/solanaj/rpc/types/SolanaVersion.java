package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SolanaVersion {

    @Json(name = "solana-core")
    private String solanaCore;

    @Json(name = "feature-set")
    private String featureSet;
}
