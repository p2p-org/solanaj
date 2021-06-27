package org.p2p.solanaj.core;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;

import java.util.List;
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
                    Map<String, Object> map = (Map<String, Object>) data;
                    String base64 = (String)((List) map.get("data")).get(0);
                    LOGGER.info(
                            String.format(
                                    "Event = %s",
                                    map
                            )
                    );
                    LOGGER.info(
                            String.format(
                                    "Data = %s",
                                    base64
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
