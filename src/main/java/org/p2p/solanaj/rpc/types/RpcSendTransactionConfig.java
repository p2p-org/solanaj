package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class RpcSendTransactionConfig {

    public enum Encoding {
        base64("base64");

        private final String enc;

        Encoding(String enc) {
            this.enc = enc;
        }

        public String getEncoding() {
            return enc;
        }

    }

    @Json(name = "encoding")
    private final Encoding encoding = Encoding.base64;

}
