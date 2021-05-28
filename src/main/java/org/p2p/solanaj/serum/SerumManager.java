package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SerumProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.util.List;
import java.util.logging.Logger;

public class SerumManager {

    private static final Logger LOGGER = Logger.getLogger(SerumManager.class.getName());
    private final RpcClient client;

    public SerumManager(final RpcClient client) {
        this.client = client;
    }

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     *
     * TODO: Currently, an open orders account is required to already exist for the given market. fix this.
     *
     * @param account Solana account to pay for the order
     * @param market Market to trade on
     * @param order Buy or sell order with quantity and price
     * @return true if the order succeeded
     */
    public String placeOrder(Account account, PublicKey payer, Market market, Order order) {
        /*
          Placing orders: A user funds an intermediary account (their OpenOrders account) from their SPL token
          account (wallet) and adds an order placement request to the Request Queue
          See: https://github.com/project-serum/serum-ts/blob/master/packages/serum/src/market.ts#L637
         */

        final Transaction transaction = new Transaction();
        final PublicKey openOrders = SerumUtils.findOpenOrdersAccountForOwner(client, market.getOwnAddress(), account.getPublicKey());

        // 0.11 SOL
        long lamports = 110000000L;
        long space = 165L;
        int matchOrdersLimit = 5;

        transaction.addInstruction(
                SerumProgram.matchOrders(
                        market,
                        matchOrdersLimit
                )
        );

        // Create payer account (only used if shouldWrapSol)
        Account payerAccount = null;

        boolean shouldWrapSol = (order.isBuy() && market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT)) ||
                (!order.isBuy() && market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT));

        if (shouldWrapSol) {
            payerAccount = new Account();
        }

        final PublicKey payerPublicKey = shouldWrapSol ? payerAccount.getPublicKey() : payer;

        if (shouldWrapSol) {
            transaction.addInstruction(
                    SystemProgram.createAccount(
                            account.getPublicKey(),
                            payerAccount.getPublicKey(),
                            lamports,
                            space,
                            TokenProgram.PROGRAM_ID
                    )
            );

            transaction.addInstruction(
                    TokenProgram.initializeAccount(
                            payerAccount.getPublicKey(),
                            SerumUtils.WRAPPED_SOL_MINT,
                            account.getPublicKey()
                    )
            );
        }

        transaction.addInstruction(
                SerumProgram.placeOrder(
                        account,
                        payerPublicKey,
                        openOrders,
                        market,
                        order
                )
        );

        if (shouldWrapSol) {
            transaction.addInstruction(
                    TokenProgram.closeAccount(
                            payerAccount.getPublicKey(),
                            account.getPublicKey(),
                            account.getPublicKey()
                    )
            );
        }

        transaction.addInstruction(
                SerumProgram.matchOrders(
                        market,
                        matchOrdersLimit
                )
        );

        transaction.addInstruction(
                MemoProgram.writeUtf8(
                        account,
                        "Order placed by SolanaJ"
                )
        );


        List<Account> signers;
        if (shouldWrapSol) {
            signers = List.of(account, payerAccount);
        } else {
            signers = List.of(account);
        }

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
            LOGGER.info("placeOrder Signature = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Cranks a given market with the ConsumeEvents instruction.
     *
     * @param market market to run crank against
     * @return transaction id of ConsumeEvents call
     */
    public String consumeEvents(Market market, Account payerAccount, List<PublicKey> openOrdersAccounts) {
        // Get all open orders accounts
        final Transaction transaction = new Transaction();

        transaction.addInstruction(
                SerumProgram.consumeEvents(
                        openOrdersAccounts,
                        payerAccount,
                        market
                )
        );

        final List<Account> signers = List.of(payerAccount);
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }



}
