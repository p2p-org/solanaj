package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;


public class SplTokenAccountInfo extends RpcResultObject {

    @Json(name = "value")
    private TokenResultObjects.Value value;

    public TokenResultObjects.Value getValue() {
        return value;
    }

}
