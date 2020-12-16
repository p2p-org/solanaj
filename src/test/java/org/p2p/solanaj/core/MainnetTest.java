package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MainnetTest {

    @Test
    public void connectToMainnet() {

        final RpcClient client = new RpcClient(Cluster.MAINNET);
        final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey);
            final double balance = (double) accountInfo.getValue().getLamports()/ 100000000;

            // Account data list
            final List<String> accountData = accountInfo.getValue().getData();

            // Verify "base64" string in accountData
            assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base64")));
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

}
