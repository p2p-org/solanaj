package org.p2p.solanaj.core;

import org.bitcoinj.core.Base58;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AccountBasedTest {

    public static Account testAccount;

    @BeforeClass
    public static void setup() {
        // Build account from secretkey.dat
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Paths.get("secretkey.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        testAccount = new Account(Base58.decode(new String(data)));
    }

}
