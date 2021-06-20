package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FeeCalculatorInfo extends RpcResultObject {

    @Getter
    @ToString
    public static class Value {

        @Json(name = "feeCalculator")
        private RecentBlockhash.FeeCalculator feeCalculator;
    }

    @Json(name = "value")
    private Value value;
}

