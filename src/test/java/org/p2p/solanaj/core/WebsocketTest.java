package org.p2p.solanaj.core;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;
import org.p2p.solanaj.ws.listeners.AccountNotificationEventListener;
import org.p2p.solanaj.ws.listeners.LogNotificationEventListener;

import static org.junit.Assert.assertTrue;

/**
 * Subscribes to a websocket, sends a transaction to the subscribed account, with an event listener.
 */
public class WebsocketTest extends AccountBasedTest {

    private final SubscriptionWebSocketClient client = SubscriptionWebSocketClient.getInstance(Cluster.MAINNET.getEndpoint());
    private final RpcClient rpcClient = new RpcClient(Cluster.MAINNET);
    private final static int AMOUNT_OF_LAMPORTS = 100;

    private static final PublicKey BTC_USDC_BIDS = new PublicKey("6wLt7CX1zZdFpa6uGJJpZfzWvG6W9rxXjquJDYiFwf9K");

    @Test
    @Ignore
    public void serumApeTest() throws InterruptedException {
        PublicKey skynet = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

        client.logsSubscribe(skynet.toBase58(), new LogNotificationEventListener(rpcClient, null));
        LOGGER.info(String.format("Listening for activity at the address: %s", skynet));

        // wait for the person to make a trade
        Thread.sleep(200000L);
    }



    @Test
    @Ignore
    public void logsSubscribeWebsocketTest() throws InterruptedException {
        client.logsSubscribe(solDestination.toBase58(), new LogNotificationEventListener(rpcClient, null));
        Thread.sleep(3000L);
        sendLamports(AMOUNT_OF_LAMPORTS);
        Thread.sleep(200000L);
        assertTrue(true);
    }

    @Test
    @Ignore
    public void orderbookWebsocketTest() {
        client.accountSubscribe(BTC_USDC_BIDS.toBase58(), new AccountNotificationEventListener());

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }


    @Test
    @Ignore
    public void websocketTest() {
        client.accountSubscribe(solDestination.toBase58(), new AccountNotificationEventListener());

        sendLamports(AMOUNT_OF_LAMPORTS);

        try {
            Thread.sleep(60000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    @Ignore
    public void raydiumSingleStakingWebsocketTest() {
        client.accountSubscribe(PublicKey.valueOf("8tnpAECxAT9nHBqR1Ba494Ar5dQMPGhL31MmPJz1zZvY").toBase58(), new AccountNotificationEventListener());


        try {
            Thread.sleep(120000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    private void sendLamports(int amount) {
        // Create account from private key
        final Account feePayer = testAccount;

        final Transaction transaction = new Transaction();
        transaction.addInstruction(
                SystemProgram.transfer(
                        feePayer.getPublicKey(),
                        solDestination,
                        amount
                )
        );

        // Call sendTransaction
        String result = null;
        try {
            result = rpcClient.getApi().sendTransaction(transaction, feePayer);
            LOGGER.info("Result = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }
}
