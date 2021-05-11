package org.p2p.solanaj.ws.listeners;

import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;

import java.util.Map;
import java.util.logging.Logger;

public class LogNotificationEventListener implements NotificationEventListener {

    private static final Logger LOGGER = Logger.getLogger(LogNotificationEventListener.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);

    /**
     * Handle Account notification event (change in data or change in lamports). Type of "data" is a Map.
     * @param data Map
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void onNotificationEvent(Object data) {
        Map map = (Map) data;

        if (map.get("logs").toString().contains("9xQeWvG816bUx9EPjHmaT23yvVM2ZWbrrpZb9PusVFin")) {
            String signature = map.get("signature").toString();
            LOGGER.info(String.format("Serum action detected in TX %s" , signature));
            try {
                ConfirmedTransaction confirmedTransaction = client.getApi().getConfirmedTransaction(signature);
                System.out.println(confirmedTransaction.getTransaction().getMessage());
            } catch (RpcException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info("EVENT = " + data.toString());
        }
    }
}
