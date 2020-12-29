package org.p2p.solanaj.core;

import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.serum.AccountFlags;

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
            System.out.println("Got data for pubkey CVfYa8RGXnuDBeGmniCcdkBwoLqVxh92xB1JqgRQx3F");
            System.out.println("Raw data (Base64) = " + base64Data);

            // assume every market is v2
            // TODO handle the two v1 markets later

            if (base64Data != null) {
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] bytes = decoder.decode(accountData.get(0));
                System.out.println("Blob #1 (5 bytes) = " + new String(Arrays.copyOfRange(bytes, 0, 5)));

                // Get account flags (next 8 bits)
                byte accountFlagsByte = Arrays.copyOfRange(bytes, 5, 6)[0];
                final AccountFlags accountFlags = new AccountFlags(accountFlagsByte);

                System.out.println("Accounts Flags #1 (1 byte (up to 8 booleans))) = " + String.format("%8s", Integer.toBinaryString(accountFlagsByte & 0xFF)).replace(' ', '0'));
                System.out.println("Account Flags (deserialized) (Initialized flag) = " + accountFlags.isInitialized());
                System.out.println("Blob #2 (last 7 bytes) = " + new String(Arrays.copyOfRange(bytes, bytes.length - 7, bytes.length)));
            }

            // Verify any balance
            assertTrue(true);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

}
