package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;

/**
 * Builds market transactions
 */
public class TransactionBuilder {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);








    public Transaction build() {
        return new Transaction();
    }
}
