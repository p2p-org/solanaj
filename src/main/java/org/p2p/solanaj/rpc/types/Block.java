package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;

public class Block {

    @Json(name = "blockTime")
    private int blockTime;

    @Json(name = "blockHeight")
    private String blockHeight;

    @Json(name = "blockhash")
    private String blockhash;

    @Json(name = "parentSlot")
    private int parentSlot;

    @Json(name = "previousBlockhash")
    private String previousBlockhash;

    @Json(name = "transactions")
    private List<ConfirmedTransaction> transactions;

    @Json(name = "rewards")
    private List<Reward> rewards;

}
