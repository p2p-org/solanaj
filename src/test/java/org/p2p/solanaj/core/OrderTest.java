package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Utils;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.serum.*;
import org.p2p.solanaj.utils.ByteUtils;

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

    @Test
    @Ignore
    public void placeOrderTest() {
        LOGGER.info("Placing order");

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
                .build();

        final Order order = new Order(1337000L, 1L, 1, 0.0f, 0.0f, null);
        order.setMaxQuoteQuantity(1337000000L);
        order.setOrderTypeLayout(OrderTypeLayout.POST_ONLY);
        order.setSelfTradeBehaviorLayout(SelfTradeBehaviorLayout.DECREMENT_TAKE);
        order.setClientId(0L);
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


        final Order usdcOrder = new Order(1, 1L, 1, 0.0f, 0.0f, null);
        usdcOrder.setMaxQuoteQuantity(100L);
        usdcOrder.setOrderTypeLayout(OrderTypeLayout.POST_ONLY);
        usdcOrder.setSelfTradeBehaviorLayout(SelfTradeBehaviorLayout.DECREMENT_TAKE);
        usdcOrder.setClientId(0L);
        usdcOrder.setBuy(true);

        final PublicKey usdcPayer = PublicKey.valueOf("A71WvME6ZhR4SFG3Ara7zQK5qdRSB97jwTVmB3sr7XiN");

        // Place order
        String usdcTransactionId = serumManager.placeOrder(
                account,
                usdcPayer,
                solUsdcMarket,
                usdcOrder
        );

        assertNotNull(usdcTransactionId);
        LOGGER.info("Successfully placed bid for 0.1 SOL on SOL/USDC market.");

    }

    // TODO - fix this
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
