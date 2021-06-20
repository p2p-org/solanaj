package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class TokenAccountInfo extends RpcResultObject {

    @Getter
    @ToString
    public static class Value {

        @Json(name = "account")
        private TokenResultObjects.Value account;

        @Json(name = "pubkey")
        private String pubkey;
    }

    @Json(name = "value")
    private List<Value> value;
}
