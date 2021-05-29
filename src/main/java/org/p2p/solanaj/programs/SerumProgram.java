package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.serum.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for creating Serum v3 {@link TransactionInstruction}s
 */
public class SerumProgram extends Program {

    private static final PublicKey TOKEN_PROGRAM_ID =
            PublicKey.valueOf("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    private static final PublicKey SYSVAR_RENT_PUBKEY =
            PublicKey.valueOf("SysvarRent111111111111111111111111111111111");

    private static final int MATCH_ORDERS_METHOD_ID = 2;
    private static final int CONSUME_EVENTS_METHOD_ID = 3;
    private static final int SETTLE_ORDERS_METHOD_ID = 5;
    private static final int CANCEL_ORDER_BY_CLIENT_ID_V2_METHOD_ID = 12;

    /**
     * Builds a {@link TransactionInstruction} to match orders for a given Market and limit.
     * Might not be needed in Serum v3, since request queue handling changed.
     *
     * @param market market to crank
     * @param limit number of orders to match
     * @return {@link TransactionInstruction} for the matchOrders call
     */
    public static TransactionInstruction matchOrders(Market market, int limit) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        accountMetas.add(new AccountMeta(market.getOwnAddress(), false, true));
        accountMetas.add(new AccountMeta(market.getRequestQueue(), false, true));
        accountMetas.add(new AccountMeta(market.getEventQueueKey(), false, true));
        accountMetas.add(new AccountMeta(market.getBids(), false, true));
        accountMetas.add(new AccountMeta(market.getAsks(), false, true));
        accountMetas.add(new AccountMeta(market.getBaseVault(), false, true));
        accountMetas.add(new AccountMeta(market.getQuoteVault(), false, true));

        byte[] transactionData = encodeMatchOrdersTransactionData(
                limit
        );

        return createTransactionInstruction(
                SerumUtils.SERUM_PROGRAM_ID_V3,
                accountMetas,
                transactionData
        );
    }

    /**
     * Encodes the limit parameter used in match orders instructions into a byte array
     *
     * @param limit number of orders to match
     * @return transaction data
     */
    private static byte[] encodeMatchOrdersTransactionData(int limit) {
        ByteBuffer result = ByteBuffer.allocate(7);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(1, (byte) MATCH_ORDERS_METHOD_ID);
        result.putShort(5, (short) limit);

        return result.array();
    }

    /**
     * Builds a {@link TransactionInstruction} to place a new v3 Serum order.
     *
     * @param account Account from private key which owns payer and openOrders
     * @param payer token pubkey funding the order. could be your USDC wallet for example.
     * @param openOrders open orders pubkey associated with this Account and market - look up using {@link SerumUtils}
     * @param market loaded market that we are trading on. this must be built by a {@link MarketBuilder}
     * @param order order we are placing
     * @return {@link TransactionInstruction} for the placeOrder call
     */
    public static TransactionInstruction placeOrder(Account account,
                                                    PublicKey payer,
                                                    PublicKey openOrders,
                                                    Market market,
                                                    Order order) {
        // pubkey: market
        final AccountMeta marketKey = new AccountMeta(market.getOwnAddress(), false, true);

        // openOrders account retrieval/creation should be done in the manager
        final AccountMeta openOrdersKey = new AccountMeta(openOrders, false, true);

        // pubkey: requestQueue
        final AccountMeta requestQueueKey = new AccountMeta(market.getRequestQueue(), false, true);

        // pubkey: eventQueue
        final AccountMeta eventQueueKey = new AccountMeta(market.getEventQueueKey(), false, true);

        // pubkey: bids
        final AccountMeta bidsKey = new AccountMeta(market.getBids(), false, true);

        // pubkey: asks
        final AccountMeta asksKey = new AccountMeta(market.getAsks(), false, true);

        // pubkey: payer
        final AccountMeta payerKey = new AccountMeta(payer, false, true);

        // pubkey: owner
        final AccountMeta ownerKey = new AccountMeta(account.getPublicKey(), true, false);

        // pubkey: baseVault
        final AccountMeta baseVaultKey = new AccountMeta(market.getBaseVault(), false, true);

        // pubkey: quoteVault
        final AccountMeta quoteVaultKey = new AccountMeta(market.getQuoteVault(), false, true);

        // pubkey: TOKEN_PROGRAM_ID
        final AccountMeta tokenProgramIdKey = new AccountMeta(TOKEN_PROGRAM_ID, false, false);

        // pubkey: SYSVAR_RENT_PUBKEY
        final AccountMeta sysvarRentKey = new AccountMeta(SYSVAR_RENT_PUBKEY, false, false);

        final List<AccountMeta> keys = List.of(
                marketKey,
                openOrdersKey,
                requestQueueKey,
                eventQueueKey,
                bidsKey,
                asksKey,
                payerKey,
                ownerKey,
                baseVaultKey,
                quoteVaultKey,
                tokenProgramIdKey,
                sysvarRentKey
        );

        byte[] transactionData =  buildNewOrderv3InstructionData(
                order
        );

        return createTransactionInstruction(SerumUtils.SERUM_PROGRAM_ID_V3, keys, transactionData);
    }

    /**
     * Encodes the {@link Order} object into a byte array usable with newOrderV3 instructions
     *
     * @param order {@link Order} object containing all required details
     * @return transaction data
     */
    public static byte[] buildNewOrderv3InstructionData(Order order) {
        ByteBuffer result = ByteBuffer.allocate(51);
        result.order(ByteOrder.LITTLE_ENDIAN);

        // Constant used to indicate newOrderv3
        SerumUtils.writeNewOrderStructLayout(result);

        // Order side (buy/sell) - enum
        SerumUtils.writeSideLayout(result, order.isBuy() ? SideLayout.BUY : SideLayout.SELL);

        // Limit price - uint64
        SerumUtils.writeLimitPrice(result, order.getPrice());

        // maxBaseQuantity - uint64 (for some reason 0.1 sol = 1L)
        SerumUtils.writeMaxBaseQuantity(result, order.getQuantity());

        // maxQuoteQuantity - uint64
        SerumUtils.writeMaxQuoteQuantity(result, order.getMaxQuoteQuantity());

        // selfTradeBehaviorLayout - selfTradeBehaviorLayout (serum-ts) - 4 bytes for a 1 byte enum
        SerumUtils.writeSelfTradeBehavior(result, order.getSelfTradeBehaviorLayout());

        // orderType - orderTypeLayout (enum)
        SerumUtils.writeOrderType(result, order.getOrderTypeLayout());

        // clientId - uint64
        SerumUtils.writeClientId(result, order.getClientOrderId());

        // "limit" - uint16 - might always be static equal to 65535
        SerumUtils.writeLimit(result);

        byte[] arrayResult = result.array();

        return arrayResult;
    }

    /**
     * Builds a {@link TransactionInstruction} to cancel an existing Serum order by client ID.
     *
     * @param market loaded market that we are trading on. this must be built by a {@link MarketBuilder}
     * @param openOrders open orders pubkey associated with this Account and market - look up using {@link SerumUtils}
     * @param owner pubkey of your SOL wallet
     * @param clientId identifier created before order creation that is associated with this order
     * @return {@link TransactionInstruction} for the cancelOrderByClientIdV2 call
     */
    public static TransactionInstruction cancelOrderByClientId(Market market,
                                                               PublicKey openOrders,
                                                               PublicKey owner,
                                                               long clientId) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        accountMetas.add(new AccountMeta(market.getOwnAddress(), false, false));
        accountMetas.add(new AccountMeta(market.getBids(), false, true));
        accountMetas.add(new AccountMeta(market.getAsks(), false, true));
        accountMetas.add(new AccountMeta(openOrders, false, true));
        accountMetas.add(new AccountMeta(owner, true, false));
        accountMetas.add(new AccountMeta(market.getEventQueueKey(), false, true));

        byte[] transactionData = encodeCancelOrderByClientIdTransactionData(
                clientId
        );

        return createTransactionInstruction(
                SerumUtils.SERUM_PROGRAM_ID_V3,
                accountMetas,
                transactionData
        );
    }

    /**
     * Encodes the clientId parameter used in cancelOrderByClientIdV2 instructions into a byte array
     *
     * @param clientId user-generated identifier associated with the order
     * @return transaction data
     */
    private static byte[] encodeCancelOrderByClientIdTransactionData(long clientId) {
        ByteBuffer result = ByteBuffer.allocate(13);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(1, (byte) CANCEL_ORDER_BY_CLIENT_ID_V2_METHOD_ID);
        result.putLong(5, clientId);

        return result.array();
    }

    /**
     * Builds a {@link TransactionInstruction} used to settle funds on a given Serum {@link Market}
     *
     * @return {@link TransactionInstruction} for the settleFunds call
     */
    public static TransactionInstruction settleFunds(Market market,
                                                     PublicKey openOrdersPubkey,
                                                     PublicKey owner,
                                                     PublicKey baseWallet,
                                                     PublicKey quoteWallet) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        accountMetas.add(new AccountMeta(market.getOwnAddress(), false, true));
        accountMetas.add(new AccountMeta(openOrdersPubkey, false, true));
        accountMetas.add(new AccountMeta(owner, true, false));
        accountMetas.add(new AccountMeta(market.getBaseVault(), false, true));
        accountMetas.add(new AccountMeta(market.getQuoteVault(), false, true));
        accountMetas.add(new AccountMeta(baseWallet, false, true));
        accountMetas.add(new AccountMeta(quoteWallet, false, true));
        accountMetas.add(new AccountMeta(SerumUtils.getVaultSigner(market), false, false));
        accountMetas.add(new AccountMeta(TOKEN_PROGRAM_ID, false, false));

        byte[] transactionData = encodeSettleOrdersTransactionData();

        return createTransactionInstruction(
                SerumUtils.SERUM_PROGRAM_ID_V3,
                accountMetas,
                transactionData
        );
    }

    /**
     * Encodes the default SettleFunds transaction data
     *
     * @return transaction data
     */
    private static byte[] encodeSettleOrdersTransactionData() {
        ByteBuffer result = ByteBuffer.allocate(5);
        result.order(ByteOrder.LITTLE_ENDIAN);
        result.put(1, (byte) SETTLE_ORDERS_METHOD_ID);
        return result.array();
    }

    // TODO: fix this, doesn't work yet
    public static TransactionInstruction consumeEvents(List<PublicKey> openOrdersAccounts,
                                                       Market market,
                                                       PublicKey baseWallet,
                                                       PublicKey quoteWallet) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        accountMetas.addAll(openOrdersAccounts.stream()
                .map(publicKey -> new AccountMeta(publicKey, false, true))
                .collect(Collectors.toList()));

        accountMetas.add(new AccountMeta(market.getOwnAddress(), false, true));
        accountMetas.add(new AccountMeta(market.getEventQueueKey(), false, true));
        accountMetas.add(new AccountMeta(baseWallet, false, true));
        accountMetas.add(new AccountMeta(quoteWallet, false, true));

        int limit = 5;
        byte[] transactionData = encodeConsumeEventsTransactionData(
                limit
        );

        return createTransactionInstruction(
                SerumUtils.SERUM_PROGRAM_ID_V3,
                accountMetas,
                transactionData
        );
    }

    /**
     * Encodes the limit parameter used in ConsumeEvents instructions into a byte array
     *
     * @param limit number of events to consume
     * @return transaction data
     */
    private static byte[] encodeConsumeEventsTransactionData(int limit) {
        ByteBuffer result = ByteBuffer.allocate(7);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(1, (byte) CONSUME_EVENTS_METHOD_ID);
        result.put(5, (byte) limit);

        return result.array();
    }
}
