package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StakeActivation {

    @Json(name = "active")
    private long active;

    @Json(name = "inactive")
    private long inactive;

    @Json(name = "state")
    private String state;
}
