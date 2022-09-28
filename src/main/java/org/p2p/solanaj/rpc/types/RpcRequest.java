package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.UUID;

public class RpcRequest {
    @Json(name = "jsonrpc")
    private final String jsonrpc = "2.0";
    @Json(name = "method")
    private final String method;
    @Json(name = "params")
    private List<Object> params = null;
    @Json(name = "id")
    private final String id = UUID.randomUUID().toString();

    public RpcRequest(String method) {
        this(method, null);
    }

    public RpcRequest(String method, List<Object> params) {
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public List<Object> getParams() {
        return params;
    }

    public String getId() {
        return id;
    }

}
