package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderTest {

    private static final Logger LOGGER = Logger.getLogger(OrderTest.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    @Test
    @Ignore
    public void placeOrderTest() {
        LOGGER.info("Placing order");

        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Account account = new Account(Base58.decode(new String(data)));

        assertEquals("F459S1MFG2whWbznzULPkYff6TFe2QjoKhgHXpRfDyCj", account.getPublicKey().toString());
        assertEquals(64, account.getSecretKey().length);

        //client.getApi().

        assertTrue(true);
    }
}
