package org.p2p.solanaj.rpc;

public enum Cluster {
    DEVNET("https://devnet.solana.com"),
    TESTNET("https://testnet.solana.com"),
    MAINNET("https://solana-api.projectserum.com");

    private String endpoint;

    Cluster(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
