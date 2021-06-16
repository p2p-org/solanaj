package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Data;

import java.util.List;

@Data
public class ConfirmedBlock {

    @Json(name = "blockTime")
    private int blockTime;

    @Json(name = "blockhash")
    private String blockhash;

    @Json(name = "parentSlot")
    private int parentSlot;

    @Json(name = "previousBlockhash")
    private String previousBlockhash;

    @Json(name = "transactions")
    private List<ConfirmedTransaction> transactions;

}
