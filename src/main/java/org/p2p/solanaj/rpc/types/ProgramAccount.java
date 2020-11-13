package org.p2p.solanaj.rpc.types;

import java.util.AbstractMap;

import com.squareup.moshi.Json;

public class ProgramAccount {

    public final class Account {
        @Json(name = "data")
        private String data;
        @Json(name = "executable")
        private boolean executable;
        @Json(name = "lamports")
        private double lamports;
        @Json(name = "owner")
        private String owner;
        @Json(name = "rentEpoch")
        private double rentEpoch;

        @SuppressWarnings({ "rawtypes" })
        public Account(Object acc) {
            AbstractMap account = (AbstractMap) acc;

            this.data = (String) account.get("data");
            this.executable = (boolean) account.get("executable");
            this.lamports = (double) account.get("lamports");
            this.owner = (String) account.get("owner");
            this.rentEpoch = (double) account.get("rentEpoch");
        }

        public String getData() {
            return data;
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

    @SuppressWarnings({ "rawtypes" })
    public ProgramAccount(AbstractMap pa) {
        this.account = (Account) new Account(pa.get("account"));
        this.pubkey = (String) pa.get("pubkey");
    }
}
