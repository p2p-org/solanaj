package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class FeeRateGovernorInfo extends RpcResultObject
{
    public static class FeeRateGovernor {
        @Json(name = "burnPercent")
        private int burnPercent;

        @Json(name = "maxLamportsPerSignature")
        private long maxLamportsPerSignature;

        @Json(name = "minLamportsPerSignature")
        private long minLamportsPerSignature;

        @Json(name = "targetLamportsPerSignature")
        private long targetLamportsPerSignature;

        @Json(name = "targetSignaturesPerSlot")
        private long targetSignaturesPerSlot;

        public int getBurnPercent() {
            return burnPercent;
        }

        public long getMaxLamportsPerSignature() {
            return maxLamportsPerSignature;
        }

        public long getMinLamportsPerSignature() {
            return minLamportsPerSignature;
        }

        public long getTargetLamportsPerSignature() {
            return targetLamportsPerSignature;
        }

        public long getTargetSignaturesPerSlot() {
            return targetSignaturesPerSlot;
        }

        @Override
        public String toString() {
            return "FeeRateGovernor{" +
                    "burnPercent=" + burnPercent +
                    ", maxLamportsPerSignature=" + maxLamportsPerSignature +
                    ", minLamportsPerSignature=" + minLamportsPerSignature +
                    ", targetLamportsPerSignature=" + targetLamportsPerSignature +
                    ", targetSignaturesPerSlot=" + targetSignaturesPerSlot +
                    '}';
        }
    }

    public static class Value {
        @Json(name = "feeRateGovernor")
        private FeeRateGovernor feeRateGovernor;

        public FeeRateGovernor getFeeRateGovernor() {
            return feeRateGovernor;
        }
    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }

}
