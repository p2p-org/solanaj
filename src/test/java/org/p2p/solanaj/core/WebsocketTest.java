package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;
import org.p2p.solanaj.ws.listeners.AccountNotificationEventListener;

import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Subscribes to a websocket, sends a transaction to the subscribed account, with an event listener.
 */
public class WebsocketTest extends AccountBasedTest {

    private final PublicKey myWallet = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");
    private final SubscriptionWebSocketClient client = SubscriptionWebSocketClient.getInstance(Cluster.MAINNET.getEndpoint());
    private final RpcClient rpcClient = new RpcClient(Cluster.MAINNET);
    private final static int AMOUNT_OF_LAMPORTS = 100;
    private static final Logger LOGGER = Logger.getLogger(WebsocketTest.class.getName());

    @Test
    public void websocketTest() {
        client.accountSubscribe(myWallet.toBase58(), new AccountNotificationEventListener());

        sendLamports(AMOUNT_OF_LAMPORTS);

        try {
            Thread.sleep(60000L);
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
                        myWallet,
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
