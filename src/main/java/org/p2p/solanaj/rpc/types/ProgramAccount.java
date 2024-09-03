package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import org.bitcoinj.core.Base58;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;

import java.util.AbstractMap;
import java.util.Base64;
import java.util.List;

public class ProgramAccount {

    public final class Account {
        @Json(name = "data")
        private String data;
        @Json(name = "executable")
        private final boolean executable;
        @Json(name = "lamports")
        private final double lamports;
        @Json(name = "owner")
        private final String owner;
        @Json(name = "rentEpoch")
        private final double rentEpoch;

        private String encoding;

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Account(Object acc) {
            AbstractMap account = (AbstractMap) acc;

            Object rawData = account.get("data");
            if (rawData instanceof List) {
                List<String> dataList = ((List<String>) rawData);

                this.data = dataList.get(0);
                this.encoding = dataList.get(1);
            } else if (rawData instanceof String) {
                this.data = (String) rawData;
            }

            this.executable = (boolean) account.get("executable");
            this.lamports = (double) account.get("lamports");
            this.owner = (String) account.get("owner");
            this.rentEpoch = (double) account.get("rentEpoch");
        }

        public String getData() {
            return data;
        }

        public byte[] getDecodedData() {
            if (encoding.equals(Encoding.base64.toString())) {
                return Base64.getDecoder().decode(data);
            }

            return Base58.decode(data);
        }

        public boolean isExecutable() {
            return executable;
        }

        public double getLamports() {
            return lamports;
        }

        public String getOwner() {
            return owner;
        }

        public double getRentEpoch() {
            return rentEpoch;
        }

    }

    @Json(name = "account")
    private Account account;
    @Json(name = "pubkey")
    private String pubkey;

    public Account getAccount() {
        return account;
    }

    public String getPubkey() {
        return pubkey;
    }

    public ProgramAccount() {
    }

    @SuppressWarnings({"rawtypes"})
    public ProgramAccount(AbstractMap pa) {
        this.account = new Account(pa.get("account"));
        this.pubkey = (String) pa.get("pubkey");
    }
}
