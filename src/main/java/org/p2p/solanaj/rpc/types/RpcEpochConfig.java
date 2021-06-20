package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class RpcEpochConfig {
        @Json(name="epoch")
        private long epoch;
        @Json(name="commitment")
        private String commitment;

        public RpcEpochConfig() {
        }

        public RpcEpochConfig(long epoch) {
            this.epoch = epoch;
        }

        public RpcEpochConfig(long epoch, String commitment) {
            this.epoch = epoch;
            this.commitment = commitment;
        }
 }