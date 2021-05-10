package org.p2p.solanaj.serum;

import java.util.logging.Logger;

public class EventQueue {

    private static final Logger LOGGER = Logger.getLogger(EventQueue.class.getName());

    private AccountFlags accountFlags;

    /**
     * Returns an {@link EventQueue} object which is built from binary data.
     *
     * @param eventQueueData binary data
     * @return built {@link EventQueue} object
     */
    public static EventQueue readEventQueue(byte[] eventQueueData) {
        // Verify that the "serum" padding exists
        EventQueue eventQueue = new EventQueue();

        SerumUtils.validateSerumData(eventQueueData);

        AccountFlags accountFlags = AccountFlags.readAccountFlags(eventQueueData);
        eventQueue.setAccountFlags(accountFlags);


        LOGGER.info("Flags = " + accountFlags);
        LOGGER.info("Padding successfully read.");

        return eventQueue;
    }

    public AccountFlags getAccountFlags() {
        return accountFlags;
    }

    public void setAccountFlags(AccountFlags accountFlags) {
        this.accountFlags = accountFlags;
    }
}
