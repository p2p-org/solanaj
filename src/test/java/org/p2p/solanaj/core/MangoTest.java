package org.p2p.solanaj.core;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Logger;

public class MangoTest {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private static final Logger LOGGER = Logger.getLogger(MangoTest.class.getName());
    private static final PublicKey SKYNET =
            new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");
    private static final PublicKey BTC_ETH_SOL_SRM_USDC_MANGO_GROUP =
            new PublicKey("2oogpTYm1sp6LPZAWD3bp2wsFpnV2kXL1s52yyFhW5vp");

    @Test
    @Ignore
    public void mangoGroupTest() throws RpcException {
        LOGGER.info(
                String.format(
                        "Looking up Mango Group %s (%s)",
                        "BTC_ETH_SOL_SRM_USDC",
                        BTC_ETH_SOL_SRM_USDC_MANGO_GROUP
                )
        );

        final AccountInfo mangoGroupAccountInfo = client.getApi().getAccountInfo(
                BTC_ETH_SOL_SRM_USDC_MANGO_GROUP
        );
        byte[] mangoGroupData = Base64.getDecoder().decode(mangoGroupAccountInfo.getValue().getData().get(0));

        // Mango groups only store 4 booleans currently, 1 byte is enough
        byte mangoGroupAccountFlags = mangoGroupData[0];

        boolean initialized = (mangoGroupAccountFlags & 1) == 1;
        boolean mangoGroup = (mangoGroupAccountFlags & 2) == 2;
        boolean marginAccount = (mangoGroupAccountFlags & 4) == 4;
        boolean mangoSrmAccount = (mangoGroupAccountFlags & 8) == 8;

        LOGGER.info(
                String.format(
                        "Mango Group: Initialized (%b), MangoGroup (%b), MarginAccount (%b), MangoSrmAccount (%b)",
                        initialized,
                        mangoGroup,
                        marginAccount,
                        mangoSrmAccount
                )
        );

        ArrayList<PublicKey> tokens = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            PublicKey tokenPubkey = PublicKey.readPubkey(mangoGroupData, 8 + (i  * 32));
            tokens.add(tokenPubkey);
            LOGGER.info(String.format("Token = %s", tokenPubkey.toBase58()));
        }

        try {
            Files.write(Path.of("mangoGroup.bin"), mangoGroupData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Lookup margin accounts for our stuff.


//        byte[] mangoData = new byte[0];
//
//        AccountInfo accountInfo = client.getApi().getAccountInfo(new PublicKey("5jdWfuPCpnooutU8M5GSRwmjtiRuieBioHNed3ddNUJq"));
//        mangoData = Base64.getDecoder().decode(accountInfo.getValue().getData().get(0));
//
//
//        try {
//            Files.write(Path.of("mangoData.dat"), mangoData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        int ACCOUNT_FLAGS_OFFSET = 0;
//        int MANGO_GROUP_OFFSET = ACCOUNT_FLAGS_OFFSET + 8;
//        int OWNER_OFFSET = MANGO_GROUP_OFFSET + 32;
//        int DEPOSITS_OFFSET = OWNER_OFFSET + 32;
//        int BORROWS_OFFSET = DEPOSITS_OFFSET + (16 * 3);
//
//        long accountFlags = Utils.readInt64(mangoData, ACCOUNT_FLAGS_OFFSET);
//        PublicKey mangoGroup = PublicKey.readPubkey(mangoData, MANGO_GROUP_OFFSET);
//        PublicKey owner = PublicKey.readPubkey(mangoData, OWNER_OFFSET);
//
//        byte[] deposits = Arrays.copyOfRange(mangoData, DEPOSITS_OFFSET, BORROWS_OFFSET);
//
//        MarginAccount marginAccount = new MarginAccount();
//        marginAccount.setAccountFlags(accountFlags);
//
//        LOGGER.info(String.format("Data length = %d", mangoData.length));
//        LOGGER.info(String.format("Flags = %s", marginAccount.getAccountFlags()));
//        LOGGER.info(String.format("Mango Group = %s", mangoGroup.toBase58()));
//        LOGGER.info(String.format("Owner = %s", owner.toBase58()));
//        LOGGER.info(String.format("Deposits = %s", Arrays.toString(deposits)));
//        LOGGER.info(String.format("Deposit 1 = %.2f, Deposit 2 = %.2f, Deposit 3 = %.2f",
//                new BigInteger(ByteUtils.bytesToHex(Arrays.copyOfRange(deposits, 0, 16)), 16).floatValue(),
//                new BigInteger(ByteUtils.bytesToHex(Arrays.copyOfRange(deposits, 16, 32)), 16).floatValue(),
//                new BigInteger(ByteUtils.bytesToHex(Arrays.copyOfRange(deposits, 32, 48)), 16).floatValue())
//        );
//
//        assertEquals(240, mangoData.length);
    }



}
