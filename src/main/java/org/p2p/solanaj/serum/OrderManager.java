package org.p2p.solanaj.serum;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        String memoMessage = "Hello from SolanaJ :)";
        TransactionInstruction memoInstruction = new TransactionInstruction(new PublicKey("Memo1UhkJRfHyvLMcVucJwxXeuD728EqVDDwQDxFMNo"), new ArrayList<AccountMeta>(), memoMessage.getBytes(StandardCharsets.UTF_8));
        transaction.addInstruction(memoInstruction);

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
