package org.p2p.solanaj.core;

import org.bitcoinj.core.Utils;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.serum.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MainnetTest {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private final PublicKey publicKey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    public static final int initialized = 1;  // Binary 00000001
    public static final int market = 2;  // Binary 00000010
    public static final int openOrders = 4;  // Binary 00000100
    public static final int requestQueue = 8;  // Binary 00001000
    public static final int eventQueue = 16;  // Binary 00010000
    public static final int bids = 32;  // Binary 00100000
    public static final int asks = 64;  // Binary 01000000

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

    @Test
    public void marketAccountTest() {
        try {
            // Pubkey of BTC/USDC market
            //final PublicKey publicKey = new PublicKey("CVfYa8RGXnuDBeGmniCcdkBwoLqVxh92xB1JqgRQx3F");
            final PublicKey publicKey = new PublicKey("FrDavxi4QawYnQY259PVfYUjUvuyPNfqSXbLBqMnbfWJ"); //FIDA/USDC

            // Get account Info
            final AccountInfo accountInfo = client.getApi().getAccountInfo(publicKey);
            final List<String> accountData = accountInfo.getValue().getData();
            final String base64Data = accountData.get(0);

            // Deserialize market from the binary data
            if (base64Data != null) {
                byte[] bytes = Base64.getDecoder().decode(accountData.get(0));
                Market market = Market.readMarket(bytes);
                System.out.println(market.toString());

                // Deserialize the bid order book. This is just proof of concept - will be moved into classes.
                // If orderbook.dat exists, use it.
                byte[] data = new byte[0];

                try {
                    data = Files.readAllBytes(Paths.get("orderbook2.dat"));
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                if (data.length == 0) {
                    AccountInfo bidAccount = client.getApi().getAccountInfo(market.getBids());
                    data = Base64.getDecoder().decode(bidAccount.getValue().getData().get(0));
                }

                OrderBook bidOrderBook = OrderBook.readOrderBook(data);
                market.setBidOrderBook(bidOrderBook);

                System.out.println(bidOrderBook.getAccountFlags().toString());

            }

            // Verify any balance
            assertTrue(true);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void orderBookTest() {
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get("orderbook.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        OrderBook bidOrderBook = OrderBook.readOrderBook(data);

        System.out.println(bidOrderBook.getAccountFlags().toString());

        Slab slab = bidOrderBook.getSlab();

        assertNotNull(slab);
        assertEquals(141, slab.getBumpIndex());
        assertEquals(78, slab.getFreeListLen());
        assertEquals(56, slab.getFreeListHead());
        assertEquals(32, slab.getLeafCount());
    }

    @Test
    public void orderBook2Test() {
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get("orderbook2.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        OrderBook bidOrderBook = OrderBook.readOrderBook(data);

        System.out.println(bidOrderBook.getAccountFlags().toString());

        Slab slab = bidOrderBook.getSlab();

        assertNotNull(slab);
//        assertEquals(67, slab.getBumpIndex());
//        assertEquals(28, slab.getFreeListLen());
//        assertEquals(22, slab.getFreeListHead());
//        assertEquals(20, slab.getLeafCount());

        slab.getSlabNodes().forEach(slabNode -> {
            if (slabNode instanceof SlabLeafNode) {
                //-6415612020026633454
                if (((SlabLeafNode)slabNode).getClientOrderId() == -6415612020026633454L) {
                    // found 3038.50      0.543320
                    System.out.println("FOUND");

                    SlabLeafNode slabLeafNode = (SlabLeafNode)slabNode;
                    long price = Utils.readInt64(slabLeafNode.getKey(), 0);

                    BigInteger bigInteger = BigInteger.valueOf(Utils.readInt64(slabLeafNode.getKey(), 0));
                    long lng = bigInteger.longValue();

                    System.out.println(Long.toUnsignedString(lng));  // 9223372036854800000



                    System.out.println("price = " + (price & 0x00000000ffffffffL));
                    System.out.println(slabLeafNode.toString());
                }
                //System.out.println(slabNode.toString());
            }
        });
    }

    public static long getUnsignedInt(int x) {
        return x & (-1L >>> 32);
    }

}
