package org.p2p.solanaj.rpc.types;

import java.util.List;

import com.squareup.moshi.Json;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountInfo extends RpcResultObject {

    @Getter
    @ToString
    public static class Value {

        @Json(name = "data")
        private List<String> data;

        @Json(name = "executable")
        private boolean executable;

        @Json(name = "lamports")
        private long lamports;

        @Json(name = "owner")
        private String owner;

        @Json(name = "rentEpoch")
        private long rentEpoch;
    }

    @Json(name = "value")
    private Value value;
}
