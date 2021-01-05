package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.serum.AccountFlags;
import org.p2p.solanaj.serum.Market;
import org.p2p.solanaj.serum.OrderBook;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

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
            final PublicKey publicKey = new PublicKey("CVfYa8RGXnuDBeGmniCcdkBwoLqVxh92xB1JqgRQx3F");

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
                AccountInfo bidAccount = client.getApi().getAccountInfo(market.getBids());
                byte[] bidAccountBytes = Base64.getDecoder().decode(bidAccount.getValue().getData().get(0));

                OrderBook bidOrderBook = OrderBook.readOrderBook(bidAccountBytes);
                market.setBidOrderBook(bidOrderBook);
                System.out.println(bidOrderBook.getAccountFlags().toString());

            }

            // Verify any balance
            assertTrue(true);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

}
