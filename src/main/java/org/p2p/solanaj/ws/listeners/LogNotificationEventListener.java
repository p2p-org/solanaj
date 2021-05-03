package org.p2p.solanaj.ws.listeners;

import org.p2p.solanaj.serum.OrderBook;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

public class LogNotificationEventListener implements NotificationEventListener {

    private static final Logger LOGGER = Logger.getLogger(LogNotificationEventListener.class.getName());

    /**
     * Handle Account notification event (change in data or change in lamports). Type of "data" is a Map.
     * @param data Map
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void onNotificationEvent(Object data) {
        LOGGER.info("EVENT = " + data.toString());
        //LOGGER.info("SIGNATURE = " + ((Map)data).get("signature"));
    }
}
