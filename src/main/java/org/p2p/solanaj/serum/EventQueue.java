package org.p2p.solanaj.serum;

import java.util.logging.Logger;

public class EventQueue {

    private static final Logger LOGGER = Logger.getLogger(EventQueue.class.getName());
    private static final String PADDING = "serum";
    private byte[] blob;

    public static EventQueue readEventQueue(byte[] base64EventQueue) {
        // Verify that the "serum" padding exists
        SerumUtils.validateSerumData(base64EventQueue);

        LOGGER.info("Padding successfully read.");

        return null;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }
}
