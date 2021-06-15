package org.p2p.solanaj.rpc.types;

import java.util.List;
import java.util.Map;

import com.squareup.moshi.Json;

import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;

public class ConfigObjects {

    public static class ConfirmedSignFAddr2 {
        @Json(name = "limit")
        private long limit;
        @Json(name = "before")
        private String before;
        @Json(name = "until")
        private String until;

        public ConfirmedSignFAddr2(int limit) {
            this.limit = limit;
        }
    }

    public static class Memcmp {
        @Json(name = "offset")
        private long offset;
        @Json(name = "bytes")
        private String bytes;

        public Memcmp() {
        }

        public Memcmp(long offset, String bytes) {
            this.offset = offset;
            this.bytes = bytes;
        }

    }

    public static class Filter {
        @Json(name = "memcmp")
        private Memcmp memcmp;

        public Filter() {
        }

        public Filter(Memcmp memcmp) {
            this.memcmp = memcmp;
        }

    }

    public static class DataSize {
        @Json(name = "dataSize")
        private int dataSize;

        public DataSize() {
        }

        public DataSize(int dataSize) {
            this.dataSize = dataSize;
        }

    }

    public static class ProgramAccountConfig {
        @Json(name = "encoding")
        private Encoding encoding = null;
        @Json(name = "filters")
        private List<Object> filters = null;
        @Json(name="commitment")
        private String commitment = "processed";

        public ProgramAccountConfig() {
        }

        public ProgramAccountConfig(List<Object> filters) {
            this.filters = filters;
        }

        public ProgramAccountConfig(Encoding encoding) {
            this.encoding = encoding;
        }

    }

    public static class RpcEpochConfig {
        @Json(name="epoch")
        private long epoch;
        @Json(name="commitment")
        private String commitment;

        public RpcEpochConfig() {
        }

        public RpcEpochConfig(long epoch) {
            this.epoch = epoch;
        }

        public RpcEpochConfig(long epoch, String commitment) {
            this.epoch = epoch;
            this.commitment = commitment;
        }
    }

    public static class SimulateTransactionConfig {
        @Json(name = "encoding")
        private Encoding encoding = Encoding.base64;
        @Json(name = "accounts")
        private Map accounts = null;
        @Json(name="commitment")
        private String commitment = "finalized";
        @Json(name = "sigVerify")
        private Boolean sigVerify = false;
        @Json(name = "replaceRecentBlockhash")
        private Boolean replaceRecentBlockhash = false;

        public SimulateTransactionConfig() {
        }

        public SimulateTransactionConfig(Map accounts) {
            this.accounts = accounts;
        }

        public SimulateTransactionConfig(Encoding encoding) {
            this.encoding = encoding;
        }

        public void setEncoding(Encoding encoding) {
            this.encoding = encoding;
        }

        public void setAccounts(Map accounts) {
            this.accounts = accounts;
        }

        public void setCommitment(String commitment) {
            this.commitment = commitment;
        }

        public void setSigVerify(Boolean sigVerify) {
            this.sigVerify = sigVerify;
        }

        public void setReplaceRecentBlockhash(Boolean replaceRecentBlockhash) {
            this.replaceRecentBlockhash = replaceRecentBlockhash;
        }
    }

    public static class ConfirmedBlockConfig {
        @Json(name = "encoding")
        private String encoding = "json";
        @Json(name = "transactionDetails")
        private String transactionDetails = "full";
        @Json(name = "rewards")
        private Boolean rewards = true;
        @Json(name = "commitment")
        private String commitment = "finalized";

        public ConfirmedBlockConfig() {
        }

        public ConfirmedBlockConfig(String encoding,
                                    String transactionDetails,
                                    Boolean rewards,
                                    String commitment) {
            this.encoding = encoding;
            this.transactionDetails = transactionDetails;
            this.rewards = rewards;
            this.commitment = commitment;
        }
    }
}
