package org.p2p.solanaj.core;

import org.bitcoinj.core.Utils;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.mango.MarginAccount;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.utils.ByteUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class MangoTest extends AccountBasedTest {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);

    @Test
    @Ignore
    public void mangoDepositsTest() {
        byte[] mangoData = new byte[0];

        try {
            AccountInfo accountInfo = client.getApi().getAccountInfo(new PublicKey("5jdWfuPCpnooutU8M5GSRwmjtiRuieBioHNed3ddNUJq"));
            mangoData = Base64.getDecoder().decode(accountInfo.getValue().getData().get(0));
        } catch (RpcException e) {
            e.printStackTrace();
        }

        try {
            Files.write(Path.of("mangoData.dat"), mangoData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int ACCOUNT_FLAGS_OFFSET = 0;
        int MANGO_GROUP_OFFSET = ACCOUNT_FLAGS_OFFSET + 8;
        int OWNER_OFFSET = MANGO_GROUP_OFFSET + 32;
        int DEPOSITS_OFFSET = OWNER_OFFSET + 32;
        int BORROWS_OFFSET = DEPOSITS_OFFSET + (16 * 3);

        long accountFlags = Utils.readInt64(mangoData, ACCOUNT_FLAGS_OFFSET);
        PublicKey mangoGroup = PublicKey.readPubkey(mangoData, MANGO_GROUP_OFFSET);
        PublicKey owner = PublicKey.readPubkey(mangoData, OWNER_OFFSET);

        byte[] deposits = Arrays.copyOfRange(mangoData, DEPOSITS_OFFSET, BORROWS_OFFSET);

        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setAccountFlags(accountFlags);

        LOGGER.info(String.format("Data length = %d", mangoData.length));
        LOGGER.info(String.format("Flags = %s", marginAccount.getAccountFlags()));
        LOGGER.info(String.format("Mango Group = %s", mangoGroup.toBase58()));
        LOGGER.info(String.format("Owner = %s", owner.toBase58()));
        LOGGER.info(String.format("Deposits = %s", Arrays.toString(deposits)));
        LOGGER.info(String.format("Deposit 1 = %.2f, Deposit 2 = %.2f, Deposit 3 = %.2f",
                new BigInteger(ByteUtils.bytesToHex(Arrays.copyOfRange(deposits, 0, 16)), 16).floatValue(),
                new BigInteger(ByteUtils.bytesToHex(Arrays.copyOfRange(deposits, 16, 32)), 16).floatValue(),
                new BigInteger(ByteUtils.bytesToHex(Arrays.copyOfRange(deposits, 32, 48)), 16).floatValue())
        );

        assertEquals(240, mangoData.length);
    }



}
