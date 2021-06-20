package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlockCommitment {

    @Json(name = "commitment")
    private long[] commitment;

    @Json(name = "totalStake")
    private long totalStake;
}
