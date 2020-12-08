package org.p2p.solanaj.core;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.bitcoinj.core.Base58;

public class AccountTest {

    @Test
    public void accountFromSecretKey() {
        byte[] secretKey = Base58
                .decode("4Z7cXSyeFR8wNGMVXUE1TwtKn5D5Vu7FzEv69dokLv7KrQk7h6pu4LF8ZRR9yQBhc7uSM6RTTZtU1fmaxiNrxXrs");
        assertEquals("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo", new Account(secretKey).getPublicKey().toString());

        assertEquals(64, new Account(secretKey).getSecretKey().length);
    }

    @Test
    public void generateNewAccount() {
        Account account = new Account();
        assertEquals(64, account.getSecretKey().length);
    }

    @Test
    public void fromMnemonic() {
        Account acc = Account.fromMnemonic(Arrays.asList("spider", "federal", "bleak", "unable", "ask", "weasel",
                "diamond", "electric", "illness", "wheat", "uphold", "mind"), "");

        assertEquals("BQWWFhzBdw2vKKBUX17NHeFbCoFQHfRARpdztPE2tDJ", acc.getPublicKey().toString());
    }

}
