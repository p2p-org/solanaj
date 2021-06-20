package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.AbstractMap;

@Getter
@ToString
@NoArgsConstructor
public class InflationReward {

    @Json(name = "epoch")
    private double epoch;

    @Json(name = "effectiveSlot")
    private double effectiveSlot;

    @Json(name = "amount")
    private double amount;

    @Json(name = "postBalance")
    private double postBalance;

    // Constructor for deserializing into List
    @SuppressWarnings({ "rawtypes" })
    public InflationReward(AbstractMap pa) {
        this.epoch = (Double) pa.get("epoch");
        this.effectiveSlot = (Double) pa.get("effectiveSlot");
        this.amount = (Double) pa.get("amount");
        this.postBalance = (Double) pa.get("postBalance");
    }
}
