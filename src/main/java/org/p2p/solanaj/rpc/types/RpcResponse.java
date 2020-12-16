package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class RpcResponse<T> {

    public static class Error {
        @Json(name = "code")
        private long code;
        @Json(name = "message")
        private String message;

        public long getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }

    @Json(name = "jsonrpc")
    private String jsonrpc;
    @Json(name = "result")
    private T result;
    @Json(name = "error")
    private Error error;
    @Json(name = "id")
    private String id;

    public Error getError() {
        return error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

}
