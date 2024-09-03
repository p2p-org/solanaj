package org.p2p.solanaj.rpc;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okhttp3.*;
import org.p2p.solanaj.rpc.types.RpcRequest;
import org.p2p.solanaj.rpc.types.RpcResponse;

import java.io.IOException;
import java.util.List;

public class RpcClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String endpoint;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final RpcApi rpcApi;

    public RpcClient(Cluster endpoint) {
        this(endpoint.getEndpoint());
    }

    public RpcClient(String endpoint) {
        this.endpoint = endpoint;
        rpcApi = new RpcApi(this);
    }

    public <T> T call(String method, List<Object> params, Class<T> clazz) throws RpcException {
        RpcRequest rpcRequest = new RpcRequest(method, params);

        JsonAdapter<RpcRequest> rpcRequestJsonAdapter = new Moshi.Builder().build().adapter(RpcRequest.class);
        JsonAdapter<RpcResponse<T>> resultAdapter = new Moshi.Builder().build()
                .adapter(Types.newParameterizedType(RpcResponse.class, clazz));

        Request request = new Request.Builder().url(endpoint)
                .post(RequestBody.create(rpcRequestJsonAdapter.toJson(rpcRequest), JSON)).build();

        try {
            Response response = httpClient.newCall(request).execute();
            RpcResponse<T> rpcResult = resultAdapter.fromJson(response.body().string());

            if (rpcResult != null && rpcResult.getError() != null) {
                throw new RpcException(rpcResult.getError().getMessage());
            }

            return rpcResult.getResult();
        } catch (IOException e) {
            throw new RpcException(e.getMessage());
        }
    }

    public RpcApi getApi() {
        return rpcApi;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
