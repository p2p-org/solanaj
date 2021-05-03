package org.p2p.solanaj.ws.listeners;

import java.util.logging.Logger;

public class AccountNotificationEventListener implements NotificationEventListener {

    private static final Logger LOGGER = Logger.getLogger(AccountNotificationEventListener.class.getName());

    @Override
    public void onNotificationEvent(Object data) {
        LOGGER.info("EVENT = " + data.toString());
    }
}
