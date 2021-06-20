package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class BlockConfig {
        @Json(name = "encoding")
        private String encoding = "json";
        @Json(name = "transactionDetails")
        private String transactionDetails = "full";
        @Json(name = "rewards")
        private Boolean rewards = true;
        @Json(name = "commitment")
        private String commitment = "finalized";

        public BlockConfig() {
        }

        public BlockConfig(String encoding,
                           String transactionDetails,
                           Boolean rewards,
                           String commitment) {
            this.encoding = encoding;
            this.transactionDetails = transactionDetails;
            this.rewards = rewards;
            this.commitment = commitment;
        }
}