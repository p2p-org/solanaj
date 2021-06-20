package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InflationRate {

    @Json(name = "total")
    private float total;

    @Json(name = "validator")
    private float validator;

    @Json(name = "foundation")
    private float foundation;

    @Json(name = "epoch")
    private long epoch;
}
