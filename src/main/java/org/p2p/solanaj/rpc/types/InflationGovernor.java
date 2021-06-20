package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
}
