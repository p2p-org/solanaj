package org.p2p.solanaj.rpc.types;

import java.util.List;

import com.squareup.moshi.Json;

public class ConfigObjects {

    public static class ConfirmedSignFAddr2 {
        @Json(name = "limit")
        private long limit;
        @Json(name = "before")
        private String before;
        @Json(name = "until")
        private String until;

        public ConfirmedSignFAddr2(int limit) {
            this.limit = limit;
        }
    }

    public static class Memcmp {
        @Json(name = "offset")
        private long offset;
        @Json(name = "bytes")
        private String bytes;

        public Memcmp() {
        }

        public Memcmp(long offset, String bytes) {
            this.offset = offset;
            this.bytes = bytes;
        }

    }

    public static class Filter {
        @Json(name = "memcmp")
        private Memcmp memcmp;

        public Filter() {
        }

        public Filter(Memcmp memcmp) {
            this.memcmp = memcmp;
        }

    }

    public static class ProgramAccountConfig {
        @Json(name = "filters")
        private List<Object> filters = null;

        public ProgramAccountConfig() {
        }

        public ProgramAccountConfig(List<Object> filters) {
            this.filters = filters;
        }
    }

    public static class ConfirmedBlockConfig {
        @Json(name = "encoding")
        private String encoding = "json";
        @Json(name = "transactionDetails")
        private String transactionDetails = "full";
        @Json(name = "rewards")
        private Boolean rewards = true;
        @Json(name = "commitment")
        private String commitment = "finalized";

        public ConfirmedBlockConfig() {
        }

        public ConfirmedBlockConfig(String encoding,
                                    String transactionDetails,
                                    Boolean rewards,
                                    String commitment) {
            this.encoding = encoding;
            this.transactionDetails = transactionDetails;
            this.rewards = rewards;
            this.commitment = commitment;
        }
    }
}
