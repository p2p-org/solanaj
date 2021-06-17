package org.p2p.solanaj.rpc.types;

import java.util.List;

import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;

import com.squareup.moshi.Json;

public class ProgramAccountConfig {
        @Json(name = "encoding")
        private Encoding encoding = null;
        @Json(name = "filters")
        private List<Object> filters = null;
        @Json(name="commitment")
        private String commitment = "processed";

        public ProgramAccountConfig() {
        }

        public ProgramAccountConfig(List<Object> filters) {
            this.filters = filters;
        }

        public ProgramAccountConfig(Encoding encoding) {
            this.encoding = encoding;
        }
}