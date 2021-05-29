package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SerumProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.util.ArrayList;
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
     * TODO: Add SRM fee discount support
     *
     * @param account Solana account to pay for the order
     * @param market Market to trade on, built by a {@link MarketBuilder}
     * @param order Order, soon to be built by OrderBuilder
     *
     * @return transaction ID for the order
     */
    public String placeOrder(Account account, Market market, Order order, PublicKey baseWallet, PublicKey quoteWallet) {
        if (order.getFloatPrice() <= 0 || order.getFloatQuantity() <= 0) {
            throw new RuntimeException("Invalid floatPrice or floatQuantity");
        }

        final Transaction transaction = new Transaction();
        final OpenOrdersAccount openOrders = SerumUtils.findOpenOrdersAccountForOwner(client, market.getOwnAddress(), account.getPublicKey());

        long longPrice = SerumUtils.priceNumberToLots(
                order.getFloatPrice(),
                market
        );

        long longQuantity = SerumUtils.baseSizeNumberToLots(
                order.getFloatQuantity(),
                market.getBaseDecimals(),
                market.getBaseLotSize()
        );

        long maxQuoteQuantity = SerumUtils.getMaxQuoteQuantity(
                order.getFloatPrice(),
                order.getFloatQuantity(),
                market
        );

        order.setPrice(longPrice);
        order.setQuantity(longQuantity);
        order.setMaxQuoteQuantity(maxQuoteQuantity);

        long lamports = SerumUtils.getLamportsNeededForSolWrapping(
                order.getFloatPrice(),
                order.getFloatQuantity(),
                order.isBuy(),
                openOrders
        );

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

        final PublicKey payerPublicKey = shouldWrapSol ? payerAccount.getPublicKey() : (order.isBuy() ? quoteWallet : baseWallet);

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
                        openOrders.getOwnPubkey(),
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

        // Instant settlement if IoC
        if (order.getOrderTypeLayout().getValue() == OrderTypeLayout.IOC.getValue()) {
            transaction.addInstruction(
                    SerumProgram.settleFunds(
                            market,
                            openOrders,
                            baseWallet,
                            quoteWallet
                    )
            );
        }

        transaction.addInstruction(
                MemoProgram.writeUtf8(
                        account.getPublicKey(),
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

    public String cancelOrderByClientId(Market market, Account owner, long clientId) {
        final Transaction transaction = new Transaction();

        // Get Open orders public key
        final OpenOrdersAccount openOrdersAccount = SerumUtils.findOpenOrdersAccountForOwner(
                client,
                market.getOwnAddress(),
                owner.getPublicKey()
        );

        transaction.addInstruction(
                SerumProgram.cancelOrderByClientId(
                        market,
                        openOrdersAccount.getOwnPubkey(),
                        owner.getPublicKey(),
                        clientId
                )
        );

        transaction.addInstruction(
                MemoProgram.writeUtf8(
                        owner.getPublicKey(),
                        "Order " + clientId + " cancelled by SolanaJ"
                )
        );

        String result = null;
        while (result == null) {
            try {
                result = client.getApi().sendTransaction(transaction, owner);
            } catch (RpcException e) {
                LOGGER.warning("Cancel order failed, trying again in 1 second");
                cancelOrderByClientId(market, owner, clientId);
            }
        }

        return result;
    }


    // TODO - create base and quote wallets if they dont exist like serum-dex-ui
    public String settleFunds(Market market, Account account, PublicKey baseWallet, PublicKey quoteWallet) {
        final Transaction transaction = new Transaction();

        // Get Open orders public key
        final OpenOrdersAccount openOrdersAccount = SerumUtils.findOpenOrdersAccountForOwner(
                client,
                market.getOwnAddress(),
                account.getPublicKey()
        );

        if (openOrdersAccount == null) {
            throw new RuntimeException("Unable to find open orders account.");
        }

        final List<Account> signers = new ArrayList<>();
        signers.add(account);

        boolean shouldWrapSol = market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT) ||
                market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT);

        Account wrappedSolAccount = null;

        if (shouldWrapSol) {
            long minimumRentBalance = 2039280L; // TODO - add option to call API for this value. put somewhere common
            long space = 165L; // TODO - put this somewhere common

            wrappedSolAccount = new Account();
            signers.add(wrappedSolAccount);

            // Create account
            transaction.addInstruction(
                    SystemProgram.createAccount(
                            account.getPublicKey(),
                            wrappedSolAccount.getPublicKey(),
                            minimumRentBalance,
                            space,
                            TokenProgram.PROGRAM_ID
                    )
            );

            // Initialize account
            transaction.addInstruction(
                    TokenProgram.initializeAccount(
                            wrappedSolAccount.getPublicKey(),
                            SerumUtils.WRAPPED_SOL_MINT,
                            account.getPublicKey()
                    )
            );
        }

        // Settle funds instruction
        transaction.addInstruction(
                SerumProgram.settleFunds(
                        market,
                        openOrdersAccount,
                        (
                                market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT) && wrappedSolAccount != null ?
                                        wrappedSolAccount.getPublicKey() :
                                        baseWallet
                        ),
                        (
                                market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT) && wrappedSolAccount != null ?
                                        wrappedSolAccount.getPublicKey() :
                                        quoteWallet
                        )
                )
        );

        if (shouldWrapSol) {
            transaction.addInstruction(
                    TokenProgram.closeAccount(
                            wrappedSolAccount.getPublicKey(),
                            account.getPublicKey(),
                            account.getPublicKey()
                    )
            );
        }

        transaction.addInstruction(
                MemoProgram.writeUtf8(
                        account.getPublicKey(),
                        "Orders settled by SolanaJ"
                )
        );

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }
}
