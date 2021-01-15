package org.p2p.solanaj.core;

import org.bitcoinj.core.Utils;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.serum.*;
import org.p2p.solanaj.utils.ByteUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class MainnetTest {

    private static final Logger LOGGER = Logger.getLogger(MainnetTest.class.getName());
    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    @Test
    public void getAccountInfoBase64() {
        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey);
            final double balance = (double) accountInfo.getValue().getLamports()/ 100000000;

            // Account data list
            final List<String> accountData = accountInfo.getValue().getData();

            // Verify "base64" string in accountData
            assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base64")));
            assertTrue(balance > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAccountInfoBase58() {
        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey, Map.of("encoding", "base58"));
            final double balance = (double) accountInfo.getValue().getLamports()/ 100000000;

            // Account data list
            final List<String> accountData = accountInfo.getValue().getData();

            // Verify "base64" string in accountData
            assertTrue(accountData.stream().anyMatch(s -> s.equalsIgnoreCase("base58")));
            assertTrue(balance > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAccountInfoRootCommitment() {
        try {
            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey, Map.of("commitment", "root"));
            final double balance = (double) accountInfo.getValue().getLamports()/ 100000000;

            // Verify any balance
            assertTrue(balance > 0);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses a {@link MarketBuilder} class to retrieve data about the BTC/USDC Serum market.
     */
    @Test
    public void marketBuilderBtcUsdcTest() {
        // Pubkey of BTC/USDC market
        final PublicKey publicKey = new PublicKey("CVfYa8RGXnuDBeGmniCcdkBwoLqVxh92xB1JqgRQx3F"); //BTC/USDC

        final Market solUsdcMarket = new MarketBuilder()
                .setPublicKey(publicKey)
                .setRetrieveOrderBooks(true)
                .build();

        final OrderBook bids = solUsdcMarket.getBidOrderBook();

        final ArrayList<Order> orders = bids.getOrders();
        orders.sort(Comparator.comparingLong(Order::getPrice).reversed());
        orders.forEach(order -> {
            LOGGER.info(order.toString());
        });

        LOGGER.info("Top bid = " + bids.getTopOrderFromBids().toString());

        // Verify any balance
        assertTrue(true);
    }

    /**
     * Verifies that {@link OrderBook} headers are properly read by {@link OrderBook#readOrderBook(byte[])}
     */
    @Test
    public void orderBookTest() {
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get("orderbook.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        OrderBook bidOrderBook = OrderBook.readOrderBook(data);

        LOGGER.info(bidOrderBook.getAccountFlags().toString());

        Slab slab = bidOrderBook.getSlab();

        assertNotNull(slab);
        assertEquals(141, slab.getBumpIndex());
        assertEquals(78, slab.getFreeListLen());
        assertEquals(56, slab.getFreeListHead());
        assertEquals(32, slab.getLeafCount());
    }

    /**
     * Will verify {@link ByteUtils} or {@link SerumUtils} can read seqNum and price.
     * Currently just reads price and logs it.
     */
    @Test
    public void testPriceDeserialization() {
        /* C:\apps\solanaj\orderbook3.dat (1/12/2021 8:55:59 AM)
   StartOffset(d): 00001277, EndOffset(d): 00001292, Length(d): 00000016 */

        byte[] rawData = {
                (byte)0xDB, (byte)0xFE, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                (byte)0xFF, (byte)0xFF, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
        };

        long price = Utils.readInt64(rawData, 0);
        BigInteger price2 = ByteUtils.readUint64(rawData, 0);
        BigInteger price3 = ByteUtils.readUint64Price(rawData, 0);
        long seqNum = Utils.readInt64(rawData, 8);

        LOGGER.info("Price = " + price + ", Price2 = " + price2 + ", Price3 = " + price3);
        LOGGER.info("seqNum = " + seqNum);


    }

    /**
     * Uses a {@link MarketBuilder} class to retrieve data about the SOL/USDC Serum market.
     */
    @Test
    public void marketBuilderSolUsdcTest() {
        final PublicKey solUsdcPublicKey = new PublicKey("7xMDbYTCqQEcK2aM9LbetGtNFJpzKdfXzLL5juaLh4GJ");

        final Market solUsdcMarket = new MarketBuilder()
                .setPublicKey(solUsdcPublicKey)
                .setRetrieveOrderBooks(true)
                .build();

        final OrderBook bids = solUsdcMarket.getBidOrderBook();
        LOGGER.info("Market = " + solUsdcMarket.toString());

        final ArrayList<Order> orders = bids.getOrders();
        orders.sort(Comparator.comparingLong(Order::getPrice).reversed());
        orders.forEach(order -> {
            LOGGER.info(order.toString());
        });
    }

}
