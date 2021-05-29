package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.serum.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class OrderTest {

    private static final Logger LOGGER = Logger.getLogger(OrderTest.class.getName());
    private final RpcClient client = new RpcClient("https://solana-api.projectserum.com");
    private final SerumManager serumManager = new SerumManager(client);
    private static final PublicKey SOL_USDC_MARKET_V3 = new PublicKey("9wFFyRfZBsuAha4YcuxcXLKwMxJR43S7fPfQLusDBzvT");

    /**
     * Places a sell order for 0.1 SOL on SOL/USDC and a buy order for 0.001 USDC on SOL/USDC.
     * This test does NOT cancel the orders, you'll need to do that manually.
     *
     * Requires open orders accounts to already be manually created beforehand.
     *
     * You'll need to configure your USDC wallet's pubkey in the "usdcPayer" variable.
     * The SOL wallet will have it's SOL wrapped automatically.
     *
     */
    @Test
    @Ignore
    public void placeOrderTest() {
//        LOGGER.info("Placing order");
//
        // Replace with the public key of your USDC wallet
        final PublicKey usdcPayer = PublicKey.valueOf("A71WvME6ZhR4SFG3Ara7zQK5qdRSB97jwTVmB3sr7XiN");

        // Build account from secretkey.dat
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create account from private key
        final Account account = new Account(Base58.decode(new String(data)));

        // Get SOL/USDC market
        final Market solUsdcMarket = new MarketBuilder()
                .setPublicKey(SOL_USDC_MARKET_V3)
                .setClient(client)
                .setRetrieveEventQueue(true)
                .build();

        long orderId = 11133711L;

        final Order order = new Order(
                1337,
                0.1f,
                orderId
        );

        order.setOrderTypeLayout(OrderTypeLayout.POST_ONLY);
        order.setSelfTradeBehaviorLayout(SelfTradeBehaviorLayout.DECREMENT_TAKE);
        order.setBuy(false);

        // Place order
        String transactionId = serumManager.placeOrder(
                account,
                null,
                solUsdcMarket,
                order
        );

        assertNotNull(transactionId);
        LOGGER.info("Successfully placed offer for 0.1 SOL on SOL/USDC market.");

        // USDC order

        long usdcOrderId = 12321L;

        final Order usdcOrder = new Order(
                0.001f,
                0.1f,
                usdcOrderId
        );

        usdcOrder.setOrderTypeLayout(OrderTypeLayout.POST_ONLY);
        usdcOrder.setSelfTradeBehaviorLayout(SelfTradeBehaviorLayout.DECREMENT_TAKE);
        usdcOrder.setBuy(true);

        // Place order
        String usdcTransactionId = serumManager.placeOrder(
                account,
                usdcPayer,
                solUsdcMarket,
                usdcOrder
        );

        assertNotNull(usdcTransactionId);
        LOGGER.info("Successfully placed bid for 0.1 SOL on SOL/USDC market.");

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cancel the SOL order
        String cancelTransactionId = serumManager.cancelOrderByClientId(
                solUsdcMarket,
                account,
                orderId
        );

        assertNotNull(cancelTransactionId);
        LOGGER.info("Cancellation TX = " + cancelTransactionId);
        LOGGER.info("Successfully cancelled order by ID " + orderId);

        // Cancel the USDC order
        String usdcCancelTransactionId = serumManager.cancelOrderByClientId(
                solUsdcMarket,
                account,
                usdcOrderId
        );

        assertNotNull(usdcCancelTransactionId);
        LOGGER.info("USDC Cancellation TX = " + cancelTransactionId);
        LOGGER.info("Successfully cancelled order by ID " + usdcOrderId);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Settle transaction
        LOGGER.info("Settling funds");
        final PublicKey baseWallet = account.getPublicKey();
        final PublicKey quoteWallet = usdcPayer;

        String settlementTransactionId = serumManager.settleFunds(
                solUsdcMarket,
                account,
                baseWallet,
                quoteWallet
        );

        assertNotNull(settlementTransactionId);
        LOGGER.info("Settlement TX = " + settlementTransactionId);
    }

    @Test
    @Ignore
    public void placeOrderOxyTest() {
        // Replace with the public key of your OXY and USDC wallet
        final PublicKey oxyWallet = PublicKey.valueOf("DoecacoZMpqHT8RGusoJYcjDFZjZauaLrDQh8BxQUVdU");
        final PublicKey usdcPayer = PublicKey.valueOf("A71WvME6ZhR4SFG3Ara7zQK5qdRSB97jwTVmB3sr7XiN");

        // Build account from secretkey.dat
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create account from private key
        final Account account = new Account(Base58.decode(new String(data)));

        // Get OXY/USDC market
        final Market oxyUsdcMarket = new MarketBuilder()
                .setPublicKey(PublicKey.valueOf("GZ3WBFsqntmERPwumFEYgrX2B7J7G11MzNZAy7Hje27X"))
                .setClient(client)
                .setRetrieveEventQueue(true)
                .build();

        long orderId = 11133711L;

        // 1 oxy bid @ $0.01
        final Order order = new Order(
                0.01f,
                1f,
                orderId
        );

        order.setOrderTypeLayout(OrderTypeLayout.POST_ONLY);
        order.setSelfTradeBehaviorLayout(SelfTradeBehaviorLayout.DECREMENT_TAKE);
        order.setBuy(true);

        // Place order
        String transactionId = serumManager.placeOrder(
                account,
                usdcPayer,
                oxyUsdcMarket,
                order
        );

        assertNotNull(transactionId);

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cancel the order
        String cancelTransactionId = serumManager.cancelOrderByClientId(
                oxyUsdcMarket,
                account,
                orderId
        );

        assertNotNull(cancelTransactionId);
        LOGGER.info("Cancellation TX = " + cancelTransactionId);
        LOGGER.info("Successfully cancelled order by ID " + orderId);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Settle transaction
        LOGGER.info("Settling funds");
        final PublicKey baseWallet = oxyWallet;
        final PublicKey quoteWallet = usdcPayer;

        String settlementTransactionId = serumManager.settleFunds(
                oxyUsdcMarket,
                account,
                baseWallet,
                quoteWallet
        );

        assertNotNull(settlementTransactionId);
        LOGGER.info("Settlement TX = " + settlementTransactionId);
    }

    @Test
    @Ignore
    public void testOpenOrdersAccounts() {
        final PublicKey skynetMainnetTestAccount = PublicKey.valueOf("F459S1MFG2whWbznzULPkYff6TFe2QjoKhgHXpRfDyCj");

        List<PublicKey> marketsToSearch = List.of(
                PublicKey.valueOf("9wFFyRfZBsuAha4YcuxcXLKwMxJR43S7fPfQLusDBzvT"),
                PublicKey.valueOf("HWHvQhFmJB3NUcu1aihKmrKegfVxBEHzwVX6yZCKEsi1"),
                PublicKey.valueOf("jyei9Fpj2GtHLDDGgcuhDacxYLLiSyxU4TY7KxB2xai"),
                PublicKey.valueOf("C6tp2RVZnxBPFbnAsfTjis8BN9tycESAT4SgDQgbbrsA"),
                PublicKey.valueOf("HCyhGnC77f7DaxQEvzj59g9ve7eJJXjsMYFWo4t7shcj")

        );

        LOGGER.info(
                String.format(
                        "Pubkey: %s\nSearching markets: %s",
                        skynetMainnetTestAccount.toBase58(),
                        marketsToSearch
                                .stream()
                                .map(PublicKey::toBase58)
                                .collect(Collectors.joining(", "))
                )
        );

        marketsToSearch.forEach(market -> {
            // get open orders account for a known pubkey
            int dataSize = 3228;

            List<ProgramAccount> programAccounts = null;

            ConfigObjects.Memcmp marketFilter = new ConfigObjects.Memcmp(SerumUtils.OWN_ADDRESS_OFFSET, market.toBase58());
            ConfigObjects.Memcmp ownerFilter = new ConfigObjects.Memcmp(45, skynetMainnetTestAccount.toBase58());

            List<ConfigObjects.Memcmp> memcmpList = List.of(marketFilter, ownerFilter);

            try {
                programAccounts = client.getApi().getProgramAccounts(SerumUtils.SERUM_PROGRAM_ID_V3, memcmpList, dataSize);
            } catch (RpcException e) {
                e.printStackTrace();
            }

            if (programAccounts != null) {
                programAccounts.forEach(programAccount -> {
                    // Get balance
                    // LOGGER.info("Open orders data = " + programAccount.getAccount().getData());

                    byte[] data = programAccount.getAccount().getDecodedData();
                    OpenOrdersAccount openOrdersAccount = OpenOrdersAccount.readOpenOrdersAccount(data);

                    boolean hasUnsettledFunds = false;

                    if (openOrdersAccount.getBaseTokenTotal() > 0 || openOrdersAccount.getQuoteTokenTotal() > 0) {
                        LOGGER.info(
                                String.format(
                                        "Found amount: %d on Market %s",
                                        openOrdersAccount.getBaseTokenTotal() + openOrdersAccount.getQuoteTokenTotal(),
                                        openOrdersAccount.getMarket()
                                )
                        );

                        // cancel orders before settlement
                        boolean isOrdersCancelled = false;

                        // cancel orders, set the bool
                        if (isOrdersCancelled) {
                            hasUnsettledFunds = true;
                        }
                    }

                    if (openOrdersAccount.getBaseTokenFree() > 0|| openOrdersAccount.getQuoteTokenFree() > 0) {
                        // settle funds
                        hasUnsettledFunds = true;
                    }

                    if (hasUnsettledFunds) {
                        LOGGER.info(String.format("Settling funds on market %s.", openOrdersAccount.getMarket()));
                    }

                    // Sleep so we don't get rate limited
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            }
        });

        assertTrue(true);
    }

    // Doesn't work yet
    @Test
    @Ignore
    public void consumeEventsTest() {
        LOGGER.info("Consuming events");

        // Build account from secretkey.dat
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create account from private key
        final Account account = new Account(Base58.decode(new String(data)));

        // Get SOL/USDC market        final Market solUsdcMarket = new MarketBuilder()
        //                .setPublicKey(SOL_USDC_MARKET_V3)
        //                .setRetrieveOrderBooks(false)
        //                .build();
        // Place order
        //String transactionId = serumManager.consumeEvents(solUsdcMarket, account);

        // Verify we got a txId
        //assertNotNull(transactionId);
    }
}
