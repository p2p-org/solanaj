package org.p2p.solanaj.rpc.types;

import java.util.List;

import com.squareup.moshi.Json;

public class AccountInfo extends RpcResultObject {

    public static class Value {
        @Json(name = "data")
        private List<String> data = null;
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
