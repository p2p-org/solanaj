package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SimulatedTransaction extends RpcResultObject {

    @Getter
    @ToString
    public static class Value {
        @Json(name = "accounts")
        private List<AccountInfo.Value> accounts;

        @Json(name = "logs")
        private List<String> logs;
    }

    @Json(name = "value")
    private Value value;
}
