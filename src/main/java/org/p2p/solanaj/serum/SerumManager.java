package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.SerumProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
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
        validateOrder(order);

        final OpenOrdersAccount openOrders = SerumUtils.findOpenOrdersAccountForOwner(client, market.getOwnAddress(), account.getPublicKey());
        validateOpenOrdersAccount(openOrders);

        return placeOrderInternal(account, market, order, baseWallet, quoteWallet, openOrders);
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
        validateOrder(order);
        validateOpenOrdersAccount(openOrdersAccount);

        return placeOrderInternal(account, market, order, baseWallet, quoteWallet, openOrdersAccount);
    }

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

        final PublicKey payerPublicKey = shouldWrapSol ? payerAccount.getPublicKey() : (order.isBuy() ? quoteWallet : baseWallet);

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

    private String settleFundsInternal(Market market, Account account, PublicKey baseWallet, PublicKey quoteWallet, OpenOrdersAccount openOrdersAccount) {
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
                        account.getPublicKey(),
                        openOrdersAccounts,
                        market,
                        baseWallet,
                        quoteWallet
                )
        );

        return sendTransactionWithSigners(transaction, List.of(account));
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

        return sendTransactionWithSigners(transaction, List.of(owner));
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

        return sendTransactionWithSigners(transaction, List.of(owner));
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
        validateOpenOrdersAccount(openOrdersAccount);
        return settleFundsInternal(market, account, baseWallet, quoteWallet, openOrdersAccount);
    }


    // TODO - create base and quote wallets if they dont exist like serum-dex-ui
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
}
