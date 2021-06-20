package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class BlockConfig {

    @Json(name = "encoding")
    private String encoding = "json";

    @Json(name = "transactionDetails")
    private String transactionDetails = "full";

    @Json(name = "rewards")
    private Boolean rewards = true;

    @Json(name = "commitment")
    private String commitment = "finalized";
}