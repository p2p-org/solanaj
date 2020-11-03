package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class RecentBlockhash extends RpcResultObject {
    public static class FeeCalculator {

        @Json(name = "lamportsPerSignature")
        private long lamportsPerSignature;

        public long getLamportsPerSignature() {
            return lamportsPerSignature;
        }

    }

    public static class Value {
        @Json(name = "blockhash")
        private String blockhash;
        @Json(name = "feeCalculator")
        private FeeCalculator feeCalculator;

        public String getBlockhash() {
            return blockhash;
        }

        public FeeCalculator getFeeCalculator() {
            return feeCalculator;
        }

    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }

    public String getRecentBlockhash() {
        return getValue().getBlockhash();
    }
}
