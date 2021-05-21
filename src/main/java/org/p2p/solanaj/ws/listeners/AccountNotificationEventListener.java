package org.p2p.solanaj.ws.listeners;

import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.serum.OrderBook;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

public class AccountNotificationEventListener implements NotificationEventListener {

    private static final Logger LOGGER = Logger.getLogger(AccountNotificationEventListener.class.getName());

    /**
     * Handle Account notification event (change in data or change in lamports). Type of "data" is a Map.
     * @param data Map
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void onNotificationEvent(Object data) {
        LOGGER.info("Raw = " + data);
//        Map tokenAmount = (Map)((Map)((Map)((Map)((Map)data).get("data")).get("parsed")).get("info")).get("tokenAmount");
//        Double uiAmount = (Double) tokenAmount.get("uiAmount");
//        LOGGER.info(String.format("Event = %.2f", uiAmount));
    }
}
