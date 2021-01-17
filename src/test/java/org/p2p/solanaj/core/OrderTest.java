package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.serum.Market;
import org.p2p.solanaj.serum.Order;
import org.p2p.solanaj.serum.OrderManager;
import org.p2p.solanaj.serum.TransactionBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderTest {

    private static final Logger LOGGER = Logger.getLogger(OrderTest.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final OrderManager orderManager = new OrderManager();
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    @Test
    @Ignore
    public void placeOrderTest() {
        LOGGER.info("Placing order");

        // Build account from secretkey.dat
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create account from private key
        final Account account = new Account(Base58.decode(new String(data)));

        // Place order
        boolean isOrderSucceeded = orderManager.placeOrder(account, new Market(), new Order(1, 1, 1));

        assertTrue(isOrderSucceeded);
    }
}
