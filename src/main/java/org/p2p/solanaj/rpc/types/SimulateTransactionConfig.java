package org.p2p.solanaj.rpc.types;

import java.util.Map;

import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;

import com.squareup.moshi.Json;

public class SimulateTransactionConfig {
        @Json(name = "encoding")
        private Encoding encoding = Encoding.base64;
        @Json(name = "accounts")
        private Map accounts = null;
        @Json(name="commitment")
        private String commitment = "finalized";
        @Json(name = "sigVerify")
        private Boolean sigVerify = false;
        @Json(name = "replaceRecentBlockhash")
        private Boolean replaceRecentBlockhash = false;

        public SimulateTransactionConfig() {
        }

        public SimulateTransactionConfig(Map accounts) {
            this.accounts = accounts;
        }

        public SimulateTransactionConfig(Encoding encoding) {
            this.encoding = encoding;
        }

        public void setEncoding(Encoding encoding) {
            this.encoding = encoding;
        }

        public void setAccounts(Map accounts) {
            this.accounts = accounts;
        }

        public void setCommitment(String commitment) {
            this.commitment = commitment;
        }

        public void setSigVerify(Boolean sigVerify) {
            this.sigVerify = sigVerify;
        }

        public void setReplaceRecentBlockhash(Boolean replaceRecentBlockhash) {
            this.replaceRecentBlockhash = replaceRecentBlockhash;
        }
}