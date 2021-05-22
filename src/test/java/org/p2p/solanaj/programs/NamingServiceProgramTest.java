package org.p2p.solanaj.programs;

import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.core.AccountBasedTest;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.naming.NamingManager;
import org.p2p.solanaj.rpc.types.AccountInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.Assert.assertTrue;

public class NamingServiceProgramTest extends AccountBasedTest {

    private final NamingManager namingManager = new NamingManager();
    private final PublicKey publicKey = testAccount.getPublicKey();
    private static final String DOMAIN_NAME = ".sol";  // testdomainname.sol
    private final PublicKey skynetMainnetPubkey = new PublicKey("skynetDj29GH6o6bAqoixCpDuYtWqi1rm8ZNx1hB3vq");

    @Test
    @Ignore
    public void createNameRegistryTest() {
        PublicKey nameClass = null; // TODO
        PublicKey parentName = new PublicKey("11111111111111111111111111111111"); // TODO

        boolean result = namingManager.createNameRegistry(DOMAIN_NAME, testAccount, publicKey, nameClass, parentName);

        assertTrue(result);
    }

    @Test
    public void retrieveNameFromRegistry() {
        // getAccountInfo
        AccountInfo testAccountInfo = namingManager.getAccountInfo(new PublicKey("BVk1qg1y9AJ3LkfWCpr8FkDXZZcu7muAyVgbTBDbqDwZ"));
        byte[] data = Base64.getDecoder().decode(testAccountInfo.getValue().getData().get(0));

        LOGGER.info(Arrays.toString(data));


        PublicKey parentName = PublicKey.readPubkey(data, 0);
        PublicKey owner = PublicKey.readPubkey(data, 32);
        PublicKey nameClass = PublicKey.readPubkey(data, 64);
        byte[] nameData = Arrays.copyOfRange(data, 64, data.length);


        LOGGER.info(String.format("parentName = %s, owner = %s, nameClass = %s", parentName, owner, nameClass));
        LOGGER.info(String.format("data = %s", Arrays.toString(nameData)));

        try {
            Files.write(Path.of("namingaccountinfo.dat"), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTwitterHandleTest() {
        String twitterHandle = namingManager.getTwitterHandle(skynetMainnetPubkey);

        LOGGER.info(twitterHandle);
        assertTrue(twitterHandle.equalsIgnoreCase("skynetcap"));
    }

    @Test
    public void twitterHandleToPubkeyLookupTest() {
        PublicKey pubkey = namingManager.getPublicKey("SBF_Alameda");

        LOGGER.info(pubkey.toBase58());
        //assertTrue(skynetMainnetPubkey.toBase58().equalsIgnoreCase(pubkey.toBase58()));
    }
}
