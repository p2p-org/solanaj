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

@SuppressWarnings("DuplicatedCode")
public class SerumManager {

    private static final Logger LOGGER = Logger.getLogger(SerumManager.class.getName());
    private final RpcClient client;

    public SerumManager(final RpcClient client) {
        this.client = client;
    }

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     * This method looks up the open orders account each time, which slows it down.
     * For speed, use the overloaded version of this which allows a pre-queried {@link OpenOrdersAccount}
     *
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

        boolean shouldWrapSol = (order.isBuy() && market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT)) ||
                (!order.isBuy() && market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT));

        long lamports = -1L;

        if (shouldWrapSol) {
            lamports = SerumUtils.getLamportsNeededForSolWrapping(
                    order.getFloatPrice(),
                    order.getFloatQuantity(),
                    order.isBuy(),
                    openOrders
            );
        }

        long space = 165L;

        // Create payer account (only used if shouldWrapSol)
        Account payerAccount = null;

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

        // Instant settlement if IoC
        // TODO - fix this if wrapped sol is used - determine which base/quote is wrapped, do the open/close account thing
        if (order.getOrderTypeLayout().getValue() == OrderTypeLayout.IOC.getValue()) {
            transaction.addInstruction(
                    SerumProgram.settleFunds(
                            market,
                            openOrders.getOwnPubkey(),
                            openOrders.getOwner(),
                            baseWallet,
                            quoteWallet
                    )
            );
        }

        List<Account> signers;
        if (shouldWrapSol) {
            signers = List.of(account, payerAccount);
        } else {
            signers = List.of(account);
        }

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     * This overloaded method allows a predetermined openorders pubkey, so we don't do the lookup everytime.
     *
     * @return transaction ID for the order
     */
    public String placeOrder(Account account,
                             Market market,
                             Order order,
                             PublicKey baseWallet,
                             PublicKey quoteWallet,
                             OpenOrdersAccount openOrdersAccount) {
        if (order.getFloatPrice() <= 0 || order.getFloatQuantity() <= 0) {
            throw new RuntimeException("Invalid floatPrice or floatQuantity");
        }

        final Transaction transaction = new Transaction();

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

        boolean shouldWrapSol = (order.isBuy() && market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT)) ||
                (!order.isBuy() && market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT));

        long lamports = -1L;

        if (shouldWrapSol) {
            lamports = SerumUtils.getLamportsNeededForSolWrapping(
                    order.getFloatPrice(),
                    order.getFloatQuantity(),
                    order.isBuy(),
                    openOrdersAccount
            );
        }

        long space = 165L;

        // Create payer account (only used if shouldWrapSol)
        Account payerAccount = null;

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
                        openOrdersAccount.getOwnPubkey(),
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

        // Instant settlement if IoC
        // NOTE - this may be buggy with wrapped SOL
        if (order.getOrderTypeLayout().getValue() == OrderTypeLayout.IOC.getValue()) {
            transaction.addInstruction(
                    SerumProgram.settleFunds(
                            market,
                            openOrdersAccount.getOwnPubkey(),
                            openOrdersAccount.getOwner(),
                            baseWallet,
                            quoteWallet
                    )
            );
        }

        List<Account> signers;
        if (shouldWrapSol) {
            signers = List.of(account, payerAccount);
        } else {
            signers = List.of(account);
        }

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
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
    public String consumeEvents(Account account,
                                Market market,
                                List<PublicKey> openOrdersAccounts,
                                PublicKey baseWallet,
                                PublicKey quoteWallet) {
        final Transaction transaction = new Transaction();

        transaction.addInstruction(
                SerumProgram.consumeEvents(
                        openOrdersAccounts,
                        market,
                        baseWallet,
                        quoteWallet
                )
        );

        final List<Account> signers = List.of(account);
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Cancels and settles an order by client id, with a pre-determined openOrdersAccount
     * Use this for speed
     *
     * @param owner
     * @param market
     * @param clientId
     * @param openOrdersAccount
     * @return
     */
    public String cancelOrderByClientIdAndSettle(Account owner,
                                                 Market market,
                                                 long clientId,
                                                 OpenOrdersAccount openOrdersAccount,
                                                 PublicKey baseWallet,
                                                 PublicKey quoteWallet) {
        final Transaction transaction = new Transaction();

        if (openOrdersAccount == null) {
            throw new RuntimeException("Unable to find open orders account.");
        }

        final List<Account> signers = new ArrayList<>();
        signers.add(owner);

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
                            owner.getPublicKey(),
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
                            owner.getPublicKey()
                    )
            );
        }

        transaction.addInstruction(
                SerumProgram.consumeEvents(
                        List.of(openOrdersAccount.getOwnPubkey()),
                        market,
                        baseWallet,
                        quoteWallet
                )
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
                SerumProgram.consumeEvents(
                        List.of(openOrdersAccount.getOwnPubkey()),
                        market,
                        baseWallet,
                        quoteWallet
                )
        );

        // Settle funds instruction
        transaction.addInstruction(
                SerumProgram.settleFunds(
                        market,
                        openOrdersAccount.getOwnPubkey(),
                        openOrdersAccount.getOwner(),
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
                            owner.getPublicKey(),
                            owner.getPublicKey()
                    )
            );
        }

        String result = null;
        while (result == null) {
            try {
                result = client.getApi().sendTransaction(transaction, owner);
            } catch (RpcException e) {
                LOGGER.warning("Cancel order failed, trying again in 1 second");
                cancelOrderByClientId(owner, market, clientId);
            }
        }

        return result;
    }

    /**
     * Cancels an order by client id, with a pre-determined openOrdersAccount
     * Use this for speed
     *
     * @param owner
     * @param market
     * @param clientId
     * @param openOrdersAccount
     * @return
     */
    public String cancelOrderByClientId(Account owner, Market market, long clientId, OpenOrdersAccount openOrdersAccount) {
        final Transaction transaction = new Transaction();

        transaction.addInstruction(
                SerumProgram.cancelOrderByClientId(
                        market,
                        openOrdersAccount.getOwnPubkey(),
                        owner.getPublicKey(),
                        clientId
                )
        );

        String result = null;
        while (result == null) {
            try {
                result = client.getApi().sendTransaction(transaction, owner);
            } catch (RpcException e) {
                LOGGER.warning("Cancel order failed, trying again in 1 second");
                cancelOrderByClientId(owner, market, clientId);
            }
        }

        return result;
    }

    public String cancelOrderByClientId(Account owner, Market market, long clientId) {
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

        String result = null;
        while (result == null) {
            try {
                result = client.getApi().sendTransaction(transaction, owner);
            } catch (RpcException e) {
                LOGGER.warning("Cancel order failed, trying again in 1 second");
                cancelOrderByClientId(owner, market, clientId);
            }
        }

        return result;
    }

    /**
     * This version takes in a pre-determined open orders account
     * Use this for speed.
     *
     * @param market
     * @param account
     * @param baseWallet
     * @param quoteWallet
     * @param openOrdersAccount
     * @return
     */
    public String settleFunds(Market market, Account account, PublicKey baseWallet, PublicKey quoteWallet, OpenOrdersAccount openOrdersAccount) {
        final Transaction transaction = new Transaction();

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
                        openOrdersAccount.getOwnPubkey(),
                        openOrdersAccount.getOwner(),
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

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
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
                        openOrdersAccount.getOwnPubkey(),
                        openOrdersAccount.getOwner(),
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

        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }
}
