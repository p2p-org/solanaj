package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RpcEpochConfig {

    @Json(name = "epoch")
    private long epoch;

    @Json(name = "commitment")
    private String commitment;

    public RpcEpochConfig(long epoch) {
        this.epoch = epoch;
    }
}