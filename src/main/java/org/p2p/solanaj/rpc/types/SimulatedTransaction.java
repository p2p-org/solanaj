package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;

public class SimulatedTransaction extends RpcResultObject {

    public static class Value {
        @Json(name = "accounts")
        private List<AccountInfo.Value> accounts;
        @Json(name = "logs")
        private List<String> logs;

        public List<AccountInfo.Value> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<AccountInfo.Value> accounts) {
            this.accounts = accounts;
        }

        public List<String> getLogs() {
            return logs;
        }

        public void setLogs(List<String> logs) {
            this.logs = logs;
        }
    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }

}
