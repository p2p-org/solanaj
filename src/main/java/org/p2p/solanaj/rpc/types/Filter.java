package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Filter {

    @Json(name = "memcmp")
    private Memcmp memcmp;
}