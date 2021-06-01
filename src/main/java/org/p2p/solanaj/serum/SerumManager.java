package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.SerumProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to send Serum-related Solana transactions with a specified {@link RpcClient}
 */
public class SerumManager {

    private final RpcClient client;

    // getMinimumBalanceForRentExemption(165) = 2039280
    private static final long MINIMUM_BALANCE_FOR_RENT_EXEMPTION_165 = 2039280L;
    private static final long REQUIRED_ACCOUNT_SPACE = 165L;

    public SerumManager(final RpcClient client) {
        this.client = client;
    }

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     * This method looks up the open orders account each time, which slows it down.
     *
     * @param account private key for the signer
     * @param market market being traded on
     * @param order order containing all required details
     * @param baseWallet base wallet to settle funds, used in IoC orders
     * @param quoteWallet quote wallet to settle funds, used in IoC orders
     * @return Solana transaction ID
     */
    public String placeOrder(Account account, Market market, Order order, PublicKey baseWallet, PublicKey quoteWallet) {
        validateOrder(order);

        final OpenOrdersAccount openOrders = SerumUtils.findOpenOrdersAccountForOwner(
                client,
                market.getOwnAddress(),
                account.getPublicKey()
        );
        validateOpenOrdersAccount(openOrders);

        return placeOrderInternal(account, market, order, baseWallet, quoteWallet, openOrders);
    }

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     * This overloaded version takes in an {@link OpenOrdersAccount}, to skip the lookup step
     *
     * @param account private key for the signer
     * @param market market being traded on
     * @param order order containing all required details
     * @param baseWallet base wallet to settle funds, used in IoC orders
     * @param quoteWallet quote wallet to settle funds, used in IoC orders
     * @param openOrdersAccount pre-determined open orders account, use {@link SerumUtils} to determine
     * @return Solana transaction ID
     */
    public String placeOrder(Account account,
                             Market market,
                             Order order,
                             PublicKey baseWallet,
                             PublicKey quoteWallet,
                             OpenOrdersAccount openOrdersAccount) {
        validateOrder(order);
        validateOpenOrdersAccount(openOrdersAccount);

        return placeOrderInternal(account, market, order, baseWallet, quoteWallet, openOrdersAccount);
    }

    /**
     * Internal logic for the overloaded placeOrder methods
     *
     * @param account private key for the signer
     * @param market market being traded on
     * @param order order containing all required details
     * @param baseWallet base wallet to settle funds, used in IoC orders
     * @param quoteWallet quote wallet to settle funds, used in IoC orders
     * @param openOrdersAccount open orders account that has already been looked up
     * @return Solana transaction ID
     */
    private String placeOrderInternal(Account account,
                                      Market market,
                                      Order order,
                                      PublicKey baseWallet,
                                      PublicKey quoteWallet,
                                      OpenOrdersAccount openOrdersAccount) {
        final Transaction transaction = new Transaction();
        setOrderPrices(order, market);

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

        // Create payer account (only used if shouldWrapSol)
        Account payerAccount = null;

        if (shouldWrapSol) {
            payerAccount = new Account();
        }

        final PublicKey payerPublicKey = shouldWrapSol ? payerAccount.getPublicKey()
                : (order.isBuy() ? quoteWallet : baseWallet);

        if (shouldWrapSol) {
            transaction.addInstruction(
                    SystemProgram.createAccount(
                            account.getPublicKey(),
                            payerAccount.getPublicKey(),
                            lamports,
                            REQUIRED_ACCOUNT_SPACE,
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
     * Internal logic for the overloaded settleFunds method
     * @param market market being traded on
     * @param account private key for the signer
     * @param baseWallet base wallet to settle funds, used in IoC orders
     * @param quoteWallet quote wallet to settle funds, used in IoC orders
     * @param openOrdersAccount open orders account that has already been looked up
     * @return Solana transaction ID
     */
    private String settleFundsInternal(Market market,
                                       Account account,
                                       PublicKey baseWallet,
                                       PublicKey quoteWallet,
                                       OpenOrdersAccount openOrdersAccount) {
        final Transaction transaction = new Transaction();
        final List<Account> signers = new ArrayList<>();
        signers.add(account);

        boolean shouldWrapSol = market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT) ||
                market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT);

        Account wrappedSolAccount = null;

        if (shouldWrapSol) {
            wrappedSolAccount = new Account();
            signers.add(wrappedSolAccount);

            // Create account
            transaction.addInstruction(
                    SystemProgram.createAccount(
                            account.getPublicKey(),
                            wrappedSolAccount.getPublicKey(),
                            MINIMUM_BALANCE_FOR_RENT_EXEMPTION_165,
                            REQUIRED_ACCOUNT_SPACE,
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

        return sendTransactionWithSigners(transaction, signers);
    }

    /**
     * Cranks a given market with the ConsumeEvents instruction.
     *
     * @param account private key for the signer
     * @param market market being traded on
     * @param openOrdersAccounts pubkeys of open orders accounts to consume
     * @param baseWallet coin fee receivable account (?)
     * @param quoteWallet pc fee receivable account (?)
     * @return Solana transaction ID
     */
    public String consumeEvents(Account account,
                                Market market,
                                List<PublicKey> openOrdersAccounts,
                                PublicKey baseWallet,
                                PublicKey quoteWallet) {
        final Transaction transaction = new Transaction();
        transaction.addInstruction(
                SerumProgram.consumeEvents(
                        account.getPublicKey(),
                        openOrdersAccounts,
                        market,
                        baseWallet,
                        quoteWallet
                )
        );

        return sendTransactionWithSigners(transaction, List.of(account));
    }

    /**
     * Cancels and settles an order by client id, with a pre-determined openOrdersAccount
     *
     * @param owner private key for the signer
     * @param market market being traded on
     * @param clientId clientId of the order we are cancelling
     * @param openOrdersAccount open orders account that has already been looked up using {@link SerumUtils}
     * @param baseWallet base wallet used for consume events/settling
     * @param quoteWallet quote wallet used for consume events/settling
     * @return Solana transaction ID
     */
    public String cancelOrderByClientIdAndSettle(Account owner,
                                                 Market market,
                                                 long clientId,
                                                 OpenOrdersAccount openOrdersAccount,
                                                 PublicKey baseWallet,
                                                 PublicKey quoteWallet) {
        validateOpenOrdersAccount(openOrdersAccount);

        final Transaction transaction = new Transaction();

        final List<Account> signers = new ArrayList<>();
        signers.add(owner);

        boolean shouldWrapSol = market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT) ||
                market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT);

        Account wrappedSolAccount = null;

        if (shouldWrapSol) {
            wrappedSolAccount = new Account();
            signers.add(wrappedSolAccount);

            // Create account
            transaction.addInstruction(
                    SystemProgram.createAccount(
                            owner.getPublicKey(),
                            wrappedSolAccount.getPublicKey(),
                            MINIMUM_BALANCE_FOR_RENT_EXEMPTION_165,
                            REQUIRED_ACCOUNT_SPACE,
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
                        owner.getPublicKey(),
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
                        owner.getPublicKey(),
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

        return sendTransactionWithSigners(transaction, signers);
    }

    /**
     * Cancels a Serum {@link Order} by clientId with a pre-determined open orders account
     *
     * @param owner private key of the signer
     * @param market market we are trading on
     * @param clientId clientId for the order we are cancelling
     * @param openOrdersAccount pre-determined open orders account
     * @return Solana transaction ID
     */
    public String cancelOrderByClientId(Account owner,
                                        Market market,
                                        long clientId,
                                        OpenOrdersAccount openOrdersAccount) {
        final Transaction transaction = new Transaction();
        transaction.addInstruction(
                SerumProgram.cancelOrderByClientId(
                        market,
                        openOrdersAccount.getOwnPubkey(),
                        owner.getPublicKey(),
                        clientId
                )
        );

        return sendTransactionWithSigners(transaction, List.of(owner));
    }

    /**
     * Cancels a Serum {@link Order} by clientId
     *
     * @param owner private key of the signer
     * @param market market we are trading on
     * @param clientId clientId for the order we are cancelling
     * @return Solana transaction ID
     */
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

        return sendTransactionWithSigners(transaction, List.of(owner));
    }

    public String cancelAllOrdersAndSettle(Account owner,
                                           Market market,
                                           OpenOrdersAccount openOrdersAccount,
                                           PublicKey baseWallet,
                                           PublicKey quoteWallet) {
        final Transaction transaction = new Transaction();
        final List<Account> signers = new ArrayList<>();
        signers.add(owner);

        for (int i = 0; i < openOrdersAccount.getClientOrderIds().size(); i++) {
            boolean isBid = ByteUtils.getBit(openOrdersAccount.getIsBidBits(), i) == 1;
            byte[] clientOrderId = openOrdersAccount.getClientOrderIds().get(i);
            SideLayout side = isBid ? SideLayout.BUY : SideLayout.SELL;

            if (clientOrderId[0] != 0) {
                transaction.addInstruction(
                        SerumProgram.cancelOrder(
                                market,
                                openOrdersAccount.getOwnPubkey(),
                                owner.getPublicKey(),
                                side,
                                clientOrderId
                        )
                );
            }
        }

        boolean shouldWrapSol = market.getQuoteMint().equals(SerumUtils.WRAPPED_SOL_MINT) ||
                market.getBaseMint().equals(SerumUtils.WRAPPED_SOL_MINT);

        Account wrappedSolAccount = null;

        if (shouldWrapSol) {
            wrappedSolAccount = new Account();
            signers.add(wrappedSolAccount);

            // Create account
            transaction.addInstruction(
                    SystemProgram.createAccount(
                            owner.getPublicKey(),
                            wrappedSolAccount.getPublicKey(),
                            MINIMUM_BALANCE_FOR_RENT_EXEMPTION_165,
                            REQUIRED_ACCOUNT_SPACE,
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

        return sendTransactionWithSigners(transaction, signers);
    }

    /**
     * Cancels a Serum {@link Order} by clientOrderId
     * clientOrderId is a unique byte array retrieved from an {@link OpenOrdersAccount}
     *
     * @param owner private key of the signer
     * @param market market we are trading on
     * @param side side of the order - buy or sell
     * @param clientOrderId byte array retrieved from an {@link OpenOrdersAccount}
     * @param openOrdersAccount pre-determined open orders account
     * @return Solana transaction ID
     */
    public String cancelOrder(Account owner,
                              Market market,
                              SideLayout side,
                              byte[] clientOrderId,
                              OpenOrdersAccount openOrdersAccount) {
        final Transaction transaction = new Transaction();

        transaction.addInstruction(
                SerumProgram.cancelOrder(
                        market,
                        openOrdersAccount.getOwnPubkey(),
                        owner.getPublicKey(),
                        side,
                        clientOrderId
                )
        );

        return sendTransactionWithSigners(transaction, List.of(owner));
    }

    /**
     * Settle funds for a given market
     *
     * @param market market we are settling funds on
     * @param account payer private key
     * @param baseWallet base destination wallet for settled funds
     * @param quoteWallet quote destination wallet for settled funds
     * @param openOrdersAccount pre-determined open orders account
     * @return Solana transaction ID
     */
    public String settleFunds(Market market,
                              Account account,
                              PublicKey baseWallet,
                              PublicKey quoteWallet,
                              OpenOrdersAccount openOrdersAccount) {
        validateOpenOrdersAccount(openOrdersAccount);
        return settleFundsInternal(market, account, baseWallet, quoteWallet, openOrdersAccount);
    }


    /**
     * Settle funds for a given market
     *
     * @param market market we are settling funds on
     * @param account payer private key
     * @param baseWallet base destination wallet for settled funds
     * @param quoteWallet quote destination wallet for settled funds
     * @return Solana transaction ID
     */
    public String settleFunds(Market market, Account account, PublicKey baseWallet, PublicKey quoteWallet) {
        final OpenOrdersAccount openOrdersAccount = SerumUtils.findOpenOrdersAccountForOwner(
                client,
                market.getOwnAddress(),
                account.getPublicKey()
        );

        validateOpenOrdersAccount(openOrdersAccount);
        return settleFundsInternal(market, account, baseWallet, quoteWallet, openOrdersAccount);
    }

    private void validateOrder(Order order) {
        if (order.getFloatPrice() <= 0 || order.getFloatQuantity() <= 0) {
            throw new RuntimeException("Invalid floatPrice or floatQuantity");
        }
    }

    private void validateOpenOrdersAccount(OpenOrdersAccount openOrdersAccount) {
        if (openOrdersAccount == null) {
            throw new RuntimeException("Unable to find open orders account.");
        }
    }

    private void setOrderPrices(Order order, Market market) {
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
    }


    private String sendTransactionWithSigners(Transaction transaction, List<Account> signers) {
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }
        return result;
    }
}
