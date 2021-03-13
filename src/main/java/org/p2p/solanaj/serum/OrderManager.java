package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SerumProgram;
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
        /*
          Placing orders: A user funds an intermediary account (their OpenOrders account) from their SPL token
          account (wallet) and adds an order placement request to the Request Queue

          See: https://github.com/project-serum/serum-ts/blob/master/packages/serum/src/market.ts#L637

          return DexInstructions.newOrder({
          market: this.address,
          requestQueue: this._decoded.requestQueue,
          baseVault: this._decoded.baseVault,
          quoteVault: this._decoded.quoteVault,
          openOrders: openOrdersAddressKey,
          owner: ownerAddress,
          payer,
          side,
          limitPrice: this.priceNumberToLots(price),
          maxQuantity: this.baseSizeNumberToLots(size),
          orderType,
          clientId,
          programId: this._programId,
          feeDiscountPubkey,
        });

         */

        /*
        // Placing orders
            let owner = new Account('...');
            let payer = new PublicKey('...'); // spl-token account
            await market.placeOrder(connection, {
              owner,
              payer,
              side: 'buy', // 'buy' or 'sell'
              price: 123.45,
              size: 17.0,
              orderType: 'limit', // 'limit', 'ioc', 'postOnly'
            });
         */

        final Transaction transaction = new Transaction();

        // PlaceOrder instruction
        transaction.addInstruction(SerumProgram.placeOrder(client, account, market, order));

        // Memo instruction (just for testing)
        //transaction.addInstruction(MemoProgram.writeUtf8(account, "Hopefully that order worked!"));

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
