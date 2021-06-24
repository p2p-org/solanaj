package org.p2p.solanaj.rpc.types.config;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignatureStatusConfig {
    @Json(name = "searchTransactionHistory")
    private final boolean searchTransactionHistory;
}
