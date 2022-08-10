package org.p2p.solanaj.rpc.types.config;

import com.squareup.moshi.Json;
import lombok.Setter;

@Setter
public class RpcSendTransactionConfig {

    public static enum Encoding {
        base64("base64");

        private String enc;

        Encoding(String enc) {
            this.enc = enc;
        }

        public String getEncoding() {
            return enc;
        }

    }

    @Json(name = "encoding")
    private Encoding encoding = Encoding.base64;

    @Json(name ="skipPreflight")
    private boolean skipPreFlight = true;

    @Json(name = "preflightCommitment")
    private String preflightCommitment = Commitment.FINALIZED.getValue();
}
