package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class VoteAccounts {

    @Getter
    @ToString
    public static class VoteAccountValue {

        @Json(name = "commission")
        private long commission;

        @Json(name = "epochVoteAccount")
        private boolean epochVoteAccount;

        @Json(name = "epochCredits")
        private List<List<Long>> epochCredits;

        @Json(name = "nodePubkey")
        private String nodePubkey;

        @Json(name = "lastVote")
        private long lastVote;

        @Json(name = "activatedStake")
        private long activatedStake;

        @Json(name = "votePubkey")
        private String votePubkey;


    }

    @Json(name = "current")
    private List<VoteAccountValue> current;

    @Json(name = "delinquent")
    private List<VoteAccountValue> delinquent;

}
