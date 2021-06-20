package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EpochSchedule {

    @Json(name = "slotsPerEpoch")
    private long slotsPerEpoch;

    @Json(name = "leaderScheduleSlotOffset")
    private long leaderScheduleSlotOffset;

    @Json(name = "warmup")
    private boolean warmup;

    @Json(name = "firstNormalEpoch")
    private long firstNormalEpoch;

    @Json(name = "firstNormalSlot")
    private long firstNormalSlot;
}
