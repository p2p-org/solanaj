package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class FeesInfo extends RpcResultObject {
    public static class Value {
        @Json(name = "blockhash")
        private String blockhash;

        @Json(name = "feeCalculator")
        private RecentBlockhash.FeeCalculator feeCalculator;

        @Json(name = "lastValidSlot")
        private long lastValidSlot;

        @Json(name = "lastValidBlockHeight")
        private long lastValidBlockHeight;

        public long getLastValidBlockHeight() {
            return lastValidBlockHeight;
        }

        public long getLastValidSlot() {
            return lastValidSlot;
        }

        public RecentBlockhash.FeeCalculator getFeeCalculator() {
            return feeCalculator;
        }

        public String getBlockhash() {
            return blockhash;
        }

        @Override
        public String toString() {
            return "FeesInfo{" +
                    "blockhash=" + blockhash +
                    ", lamportsPerSignature=" + feeCalculator.getLamportsPerSignature() +
                    ", lastValidSlot=" + lastValidSlot +
                    ", lastValidBlockHeight=" + lastValidBlockHeight +
                    '}';
        }
    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }
}
