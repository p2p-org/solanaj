package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.serum.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class OrderTest {

    private static final Logger LOGGER = Logger.getLogger(OrderTest.class.getName());
    private final OrderManager orderManager = new OrderManager();

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

        // Get SOL/USDC market
        final Market solUsdcMarket = new MarketBuilder()
                .setPublicKey(new PublicKey("7xMDbYTCqQEcK2aM9LbetGtNFJpzKdfXzLL5juaLh4GJ"))
                .build();

        // Place order
        String transactionId = orderManager.placeOrder(account, solUsdcMarket, new Order(1, 1, 1));

        // Verify we got a txId
        assertNotNull(transactionId);
    }
}
