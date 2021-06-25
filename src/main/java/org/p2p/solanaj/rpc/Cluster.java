package org.p2p.solanaj.rpc;

public enum Cluster {
    DEVNET("https://api.devnet.solana.com"),
    TESTNET("https://api.testnet.solana.com"),
    MAINNET("https://api.mainnet-beta.solana.com");

    private String endpoint;

    Cluster(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
