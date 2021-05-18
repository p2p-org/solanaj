package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class SplTokenAccountInfo extends RpcResultObject {

    public static class TokenAmountInfo {
        @Json(name = "amount")
        private Long amount;

        @Json(name = "decimals")
        private Integer decimals;

        @Json(name = "uiAmount")
        private Double uiAmount;

        @Json(name = "uiAmountString")
        private String uiAmountString;
    }

    public static class TokenInfo {
        @Json(name = "isNative")
        private Boolean isNative;

        @Json(name = "mint")
        private String mint;

        @Json(name = "owner")
        private String owner;

        @Json(name = "state")
        private String state;

        @Json(name = "tokenAmount")
        private TokenAmountInfo tokenAmount = null;

        public Boolean getNative() {
            return isNative;
        }

        public String getMint() {
            return mint;
        }

        public String getOwner() {
            return owner;
        }

        public String getState() {
            return state;
        }

        public TokenAmountInfo getTokenAmount() {
            return tokenAmount;
        }
    }

    public static class ParsedData {
        @Json(name = "info")
        private TokenInfo info = null;

        @Json(name = "type")
        private String type;

        public TokenInfo getInfo() {
            return info;
        }

        public String getType() {
            return type;
        }
    }

    public static class Data {
        @Json(name = "parsed")
        private ParsedData parsed = null;

        @Json(name = "program")
        private String program;

        @Json(name = "space")
        private Integer space;

        public ParsedData getParsed() {
            return parsed;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public Integer getSpace() {
            return space;
        }

        public void setSpace(Integer space) {
            this.space = space;
        }
    }

    public static class Value {
        @Json(name = "data")
        private Data data = null;
        @Json(name = "executable")
        private boolean executable;
        @Json(name = "lamports")
        private long lamports;
        @Json(name = "owner")
        private String owner;
        @Json(name = "rentEpoch")
        private long rentEpoch;

        public Data getData() {
            return data;
        }

        public boolean isExecutable() {
            return executable;
        }

        public long getLamports() {
            return lamports;
        }

        public String getOwner() {
            return owner;
        }

        public long getRentEpoch() {
            return rentEpoch;
        }

    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }

}
