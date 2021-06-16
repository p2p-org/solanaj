package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;

public class TokenAccountInfo extends RpcResultObject {

    public static class Value {

        @Json(name = "account")
        private TokenResultObjects.Value account;

        @Json(name = "pubkey")
        private String pubkey;

        public TokenResultObjects.Value getAccount() {
            return account;
        }

        public String getPubkey() {
            return pubkey;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "account=" + account +
                    ", pubkey='" + pubkey + '\'' +
                    '}';
        }
    }

    @Json(name = "value")
    private List<Value> value;

    public List<Value> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TokenAccountInfo{" +
                "value=" + value +
                '}';
    }
}
