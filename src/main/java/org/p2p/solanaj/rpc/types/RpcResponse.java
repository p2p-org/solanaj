package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RpcResponse<T> {

    @Getter
    @ToString
    public static class Error {
        @Json(name = "code")
        private long code;

        @Json(name = "message")
        private String message;
    }

    @Json(name = "jsonrpc")
    private String jsonrpc;

    @Json(name = "result")
    private T result;

    @Json(name = "error")
    private Error error;

    @Json(name = "id")
    private String id;
}
