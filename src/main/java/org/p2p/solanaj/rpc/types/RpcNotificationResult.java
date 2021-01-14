package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class RpcNotificationResult {

    public static class Result extends RpcResultObject {
        @Json(name = "value")
        private Object value;

        public Object getValue() {
            return value;
        }
    }

    public static class Params {

        @Json(name = "result")
        private Result result;
        @Json(name = "subscription")
        private long subscription;

        public Result getResult() {
            return result;
        }

        public long getSubscription() {
            return subscription;
        }

    }

    @Json(name = "jsonrpc")
    private String jsonrpc;
    @Json(name = "method")
    private String method;
    @Json(name = "params")
    private Params params;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public Params getParams() {
        return params;
    }

}
