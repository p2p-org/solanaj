package org.p2p.solanaj.core;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;

import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class WebsocketTest {

    private final SubscriptionWebSocketClient devnetClient = SubscriptionWebSocketClient.getInstance(
            Cluster.DEVNET.getEndpoint()
    );
    private static final Logger LOGGER = Logger.getLogger(WebsocketTest.class.getName());

    @Test
    @Ignore
    public void pythWebsocketTest() {
        devnetClient.accountSubscribe(
                PublicKey.valueOf("E36MyBbavhYKHVLWR79GiReNNnBDiHj6nWA7htbkNZbh").toBase58(),
                data -> {
                    Map<String, String> map = (Map<String, String>) data;
                    LOGGER.info(
                            String.format(
                                    "Event = %s",
                                    map
                            )
                    );
                }
        );

        try {
            Thread.sleep(120000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
