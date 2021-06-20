package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RpcNotificationResult {

    @Getter
    @ToString
    public static class Result extends RpcResultObject {

        @Json(name = "value")
        private Object value;
    }

    @Getter
    @ToString
    public static class Params {

        @Json(name = "result")
        private Result result;

        @Json(name = "subscription")
        private long subscription;
    }

    @Json(name = "jsonrpc")
    private String jsonrpc;

    @Json(name = "method")
    private String method;

    @Json(name = "params")
    private Params params;
}
