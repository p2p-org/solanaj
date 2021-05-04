package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AccountBasedTest {

    public static Account testAccount;
    public static PublicKey solDestination;
    public static PublicKey usdcSource;

    @BeforeClass
    public static void setup() {
        // Build account from secretkey.dat
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read test.solana.pubkey and test.solana.pubkey.source.usdc
        try (InputStream input = new FileInputStream("solanaj.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            solDestination = new PublicKey(properties.getProperty("test.solana.pubkey"));
            usdcSource = new PublicKey(properties.getProperty("test.solana.pubkey.source.usdc"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        testAccount = new Account(Base58.decode(new String(data)));
    }

}
