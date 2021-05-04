package org.p2p.solanaj.programs;

import org.junit.Test;
import org.p2p.solanaj.core.AccountBasedTest;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.naming.NamingManager;

import static org.junit.Assert.assertTrue;

public class NamingServiceProgramTest extends AccountBasedTest {

    private final NamingManager namingManager = new NamingManager();
    private final PublicKey publicKey = testAccount.getPublicKey();
    private static final String DOMAIN_NAME = "testdomainname";  // testdomainname.sol

    @Test
    public void createNameRegistryTest() {
        PublicKey nameClass = null; // TODO
        PublicKey parentName = null; // TODO

        boolean result = namingManager.createNameRegistry(DOMAIN_NAME, testAccount, publicKey, nameClass, parentName);

        assertTrue(result);
    }
}
