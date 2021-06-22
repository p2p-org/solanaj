package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SignatureStatuses extends RpcResultObject {

    @Getter
    @ToString
    public static class Value {
        @Json(name = "slot")
        private long slot;

        @Json(name = "confirmations")
        private Long confirmations;

        @Json(name = "confirmationStatus")
        private String confirmationStatus;
    }

    @Json(name = "value")
    private List<Value> value;
}
