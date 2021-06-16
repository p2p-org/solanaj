package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.AbstractMap;

public class TokenResultObjects {

    public static class TokenAmountInfo {

        @Json(name = "amount")
        private String amount;

        @Json(name = "decimals")
        private int decimals;

        @Json(name = "uiAmount")
        private Double uiAmount;

        @Json(name = "uiAmountString")
        private String uiAmountString;

        public TokenAmountInfo() {
        }

        public TokenAmountInfo(String amount, int decimals, Double uiAmount, String uiAmountString) {
            this.amount = amount;
            this.decimals = decimals;
            this.uiAmount = uiAmount;
            this.uiAmountString = uiAmountString;
        }

        public TokenAmountInfo(AbstractMap am) {
            this.amount = (String) am.get("amount");
            this.decimals = (int) (double) am.get("decimals");
            this.uiAmount = (Double) am.get("uiAmount");
            this.uiAmountString = (String) am.get("uiAmountString");
        }

        public String getAmount() {
            return amount;
        }

        public int getDecimals() {
            return decimals;
        }

        public Double getUiAmount() {
            return uiAmount;
        }

        public String getUiAmountString() {
            return uiAmountString;
        }

        @Override
        public String toString() {
            return "TokenAmount{" +
                    "amount='" + amount + '\'' +
                    ", decimals=" + decimals +
                    ", uiAmount=" + uiAmount +
                    ", uiAmountString='" + uiAmountString + '\'' +
                    '}';
        }
    }

    public static class TokenAccount extends TokenAmountInfo {

        @Json(name = "address")
        private String address;

        public TokenAccount() {
        }

        public TokenAccount(AbstractMap am) {
            super(am);
            this.address = (String) am.get("address");
        }

        public String getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "TokenAccount{" +
                    "address='" + address + '\'' +
                    ", amount='" + getAmount() + '\'' +
                    ", decimals=" + getDecimals() +
                    ", uiAmount=" + getUiAmount() +
                    ", uiAmountString='" + getUiAmountString() + '\'' +
                    '}';
        }
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
        private TokenAmountInfo tokenAmount;

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

        @Override
        public String toString() {
            return "TokenInfo{" +
                    "isNative=" + isNative +
                    ", mint='" + mint + '\'' +
                    ", owner='" + owner + '\'' +
                    ", state='" + state + '\'' +
                    ", tokenAmount=" + tokenAmount +
                    '}';
        }
    }

    public static class ParsedData {

        @Json(name = "info")
        private TokenInfo info;

        @Json(name = "type")
        private String type;

        public TokenInfo getInfo() {
            return info;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "ParsedData{" +
                    "info=" + info +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static class Data {

        @Json(name = "parsed")
        private ParsedData parsed;

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

        public Integer getSpace() {
            return space;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "parsed=" + parsed +
                    ", program='" + program + '\'' +
                    ", space=" + space +
                    '}';
        }
    }

    public static class Value {

        @Json(name = "data")
        private Data data;

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

        @Override
        public String toString() {
            return "Value{" +
                    "data=" + data +
                    ", executable=" + executable +
                    ", lamports=" + lamports +
                    ", owner='" + owner + '\'' +
                    ", rentEpoch=" + rentEpoch +
                    '}';
        }
    }

}
