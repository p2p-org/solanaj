package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Memcmp {

    @Json(name = "offset")
    private long offset;

    @Json(name = "bytes")
    private String bytes;
}