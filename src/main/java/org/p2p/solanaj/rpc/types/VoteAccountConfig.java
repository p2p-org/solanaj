package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VoteAccountConfig {
    @Json(name = "votePubkey")
    private  String votePubkey;
}
