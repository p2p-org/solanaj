package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class FeeCalculatorInfo extends RpcResultObject {
    public static class Value {
        @Json(name = "feeCalculator")
        private RecentBlockhash.FeeCalculator feeCalculator;

        public RecentBlockhash.FeeCalculator getFeeCalculator() {
            return feeCalculator;
        }
    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }
}

