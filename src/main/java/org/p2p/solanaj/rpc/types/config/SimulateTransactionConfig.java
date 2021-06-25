package org.p2p.solanaj.rpc.types.config;

import java.util.Map;

import lombok.Setter;
import org.p2p.solanaj.rpc.types.config.RpcSendTransactionConfig.Encoding;

import com.squareup.moshi.Json;

@Setter
public class SimulateTransactionConfig {

    @Json(name = "encoding")
    private Encoding encoding = Encoding.base64;

    @Json(name = "accounts")
    private Map accounts = null;

    @Json(name = "commitment")
    private String commitment = "finalized";

    @Json(name = "sigVerify")
    private Boolean sigVerify = false;

    @Json(name = "replaceRecentBlockhash")
    private Boolean replaceRecentBlockhash = false;

    public SimulateTransactionConfig(Map accounts) {
        this.accounts = accounts;
    }

    public SimulateTransactionConfig(Encoding encoding) {
        this.encoding = encoding;
    }
}