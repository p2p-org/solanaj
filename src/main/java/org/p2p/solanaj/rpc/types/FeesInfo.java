package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FeesInfo extends RpcResultObject {

    @Getter
    @ToString
    public static class Value {

        @Json(name = "blockhash")
        private String blockhash;

        @Json(name = "feeCalculator")
        private RecentBlockhash.FeeCalculator feeCalculator;

        @Json(name = "lastValidSlot")
        private long lastValidSlot;

        @Json(name = "lastValidBlockHeight")
        private long lastValidBlockHeight;
    }

    @Json(name = "value")
    private Value value;
}
