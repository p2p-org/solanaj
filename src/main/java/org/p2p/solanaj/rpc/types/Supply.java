package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import org.p2p.solanaj.core.PublicKey;

import java.util.List;

public class Supply extends RpcResultObject {

    public static class Value {
        @Json(name = "total")
        private long total;

        @Json(name = "circulating")
        private long circulating;

        @Json(name = "nonCirculating")
        private long nonCirculating;

        @Json(name = "nonCirculatingAccounts")
        private List<String> nonCirculatingAccounts;

        public long getTotal() {
            return total;
        }

        public long getCirculating() {
            return circulating;
        }

        public long getNonCirculating() {
            return nonCirculating;
        }

        public List<String> getNonCirculatingAccounts() {
            return nonCirculatingAccounts;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "total=" + total +
                    ", circulating=" + circulating +
                    ", nonCirculating=" + nonCirculating +
                    ", nonCirculatingAccounts=" + nonCirculatingAccounts +
                    '}';
        }
    }

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Supply{" +
                "value=" + value +
                '}';
    }
}
