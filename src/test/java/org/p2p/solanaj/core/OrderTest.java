package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;

import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class OrderTest {

    private static final Logger LOGGER = Logger.getLogger(OrderTest.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    @Test
    public void placeOrderTest() {
        LOGGER.info("Placing order");

        //client.getApi().

        assertTrue(true);
    }
}
