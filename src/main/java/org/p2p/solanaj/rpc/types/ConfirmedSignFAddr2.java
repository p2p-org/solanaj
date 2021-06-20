package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class ConfirmedSignFAddr2 {

    @Json(name = "limit")
    private long limit;

    @Json(name = "before")
    private String before;

    @Json(name = "until")
    private String until;

    public ConfirmedSignFAddr2(int limit) {
        this.limit = limit;
    }
}