package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;

public class AccountInfo extends RpcResultObject {

    public static class Value {
        @Json(name = "data")
        private final List<String> data = null;
        @Json(name = "executable")
        private boolean executable;
        @Json(name = "lamports")
        private long lamports;
        @Json(name = "owner")
        private String owner;
        @Json(name = "rentEpoch")
        private long rentEpoch;

        public List<String> getData() {
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
