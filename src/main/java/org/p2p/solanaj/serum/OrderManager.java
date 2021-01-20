package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.util.logging.Logger;

public class OrderManager {

    private static final Logger LOGGER = Logger.getLogger(OrderManager.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     * @param account Solana account to pay for the order
     * @param market Market to trade on
     * @param order Buy or sell order with quantity and price
     * @return true if the order succeeded
     */
    public String placeOrder(Account account, Market market, Order order) {

        final Transaction transaction = new Transaction();
        try {
            transaction.setRecentBlockHash(client.getApi().getRecentBlockhash());
        } catch (RpcException e) {
            e.printStackTrace();
        }

        transaction.addInstruction(MemoProgram.writeUtf8("Hello from SolanaJ :)"));

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, account);
            LOGGER.info("Result = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }



}
