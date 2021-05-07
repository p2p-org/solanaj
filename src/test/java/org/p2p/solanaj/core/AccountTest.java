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

    @Test
    public void fromJson() {
        String json = "[94,151,102,217,69,77,121,169,76,7,9,241,196,119,233,67,25,222,209,40,113,70,33,81,154,33,136,30,208,45,227,28,23,245,32,61,13,33,156,192,84,169,95,202,37,105,150,21,157,105,107,130,13,134,235,7,16,130,50,239,93,206,244,0]";
        Account acc = Account.fromJson(json);

        assertEquals("2cXAj2TagK3t6rb2CGRwyhF6sTFJgLyzyDGSWBcGd8Go", acc.getPublicKey().toString());
    }

}
