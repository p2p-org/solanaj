package org.p2p.solanaj.core;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class PublicKeyTest {

    @Test(expected = IllegalArgumentException.class)
    public void ivalidKeys() {
        new PublicKey(new byte[]{3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0});
        new PublicKey("300000000000000000000000000000000000000000000000000000000000000000000");
        new PublicKey("300000000000000000000000000000000000000000000000000000000000000");
    }

    @Test
    public void validKeys() {
        PublicKey key = new PublicKey(new byte[]{3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,});
        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", key.toString());

        PublicKey key1 = new PublicKey("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3");
        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", key1.toBase58());

        PublicKey key2 = new PublicKey("11111111111111111111111111111111");
        assertEquals("11111111111111111111111111111111", key2.toBase58());

        byte[] byteKey = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1,};
        PublicKey key3 = new PublicKey(byteKey);
        assertArrayEquals(byteKey, new PublicKey(key3.toBase58()).toByteArray());
    }

    @Test
    public void equalsKey() {
        PublicKey key = new PublicKey("11111111111111111111111111111111");

        assertFalse(key.equals(new PublicKey("11111111111111111111111111111112")));
    }

    @Test
    public void readPubkey() {
        PublicKey key = new PublicKey("11111111111111111111111111111111");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(1);

        PublicKey.writeBytes(key.toByteArray(), bos);

        byte[] bytes = bos.toByteArray();
        assertEquals(key.toString(), PublicKey.readPubkey(bytes, 1).toString());
    }

    @Test
    public void createProgramAddress() throws Exception {
        PublicKey programId = new PublicKey("BPFLoader1111111111111111111111111111111111");

        PublicKey programAddress = PublicKey.createProgramAddress(
                Collections.singletonList(new PublicKey("SeedPubey1111111111111111111111111111111111").toByteArray()), programId);
        assertTrue(programAddress.equals(new PublicKey("GUs5qLUfsEHkcMB9T38vjr18ypEhRuNWiePW2LoK4E3K")));

        programAddress = PublicKey.createProgramAddress(Arrays.asList("".getBytes(), new byte[]{1}), programId);
        assertTrue(programAddress.equals(new PublicKey("3gF2KMe9KiC6FNVBmfg9i267aMPvK37FewCip4eGBFcT")));

        programAddress = PublicKey.createProgramAddress(Collections.singletonList("☉".getBytes()), programId);
        assertTrue(programAddress.equals(new PublicKey("7ytmC1nT1xY4RfxCV2ZgyA7UakC93do5ZdyhdF3EtPj7")));

        programAddress = PublicKey.createProgramAddress(Arrays.asList("Talking".getBytes(), "Squirrels".getBytes()),
                programId);
        assertTrue(programAddress.equals(new PublicKey("HwRVBufQ4haG5XSgpspwKtNd3PC9GM9m1196uJW36vds")));

        PublicKey programAddress2 = PublicKey.createProgramAddress(Collections.singletonList("Talking".getBytes()), programId);
        assertFalse(programAddress.equals(programAddress2));
    }

    @Test
    public void findProgramAddress() throws Exception {
        PublicKey programId = new PublicKey("BPFLoader1111111111111111111111111111111111");

        PublicKey.ProgramDerivedAddress programAddress = PublicKey.findProgramAddress(Collections.singletonList("".getBytes()), programId);
        assertTrue(programAddress.getAddress().equals(PublicKey.createProgramAddress(
                Arrays.asList("".getBytes(), new byte[]{(byte) programAddress.getNonce()}), programId)));

    }

    @Test
    public void findProgramAddress1() throws Exception {
        PublicKey programId = new PublicKey("6Cust2JhvweKLh4CVo1dt21s2PJ86uNGkziudpkNPaCj");
        PublicKey programId2 = new PublicKey("BPFLoader1111111111111111111111111111111111");

        PublicKey.ProgramDerivedAddress programAddress = PublicKey.findProgramAddress(
                Collections.singletonList(new PublicKey("8VBafTNv1F8k5Bg7DTVwhitw3MGAMTmekHsgLuMJxLC8").toByteArray()), programId);
        assertTrue(programAddress.getAddress().equals(new PublicKey("FGnnqkzkXUGKD7wtgJCqTemU3WZ6yYqkYJ8xoQoXVvUG")));

        PublicKey.ProgramDerivedAddress programAddress2 = PublicKey
                .findProgramAddress(
                        Arrays.asList(new PublicKey("SeedPubey1111111111111111111111111111111111").toByteArray(),
                                new PublicKey("3gF2KMe9KiC6FNVBmfg9i267aMPvK37FewCip4eGBFcT").toByteArray(),
                                new PublicKey("HwRVBufQ4haG5XSgpspwKtNd3PC9GM9m1196uJW36vds").toByteArray()),
                        programId2);
        assertTrue(programAddress2.getAddress().equals(new PublicKey("GXLbx3CbJuTTtJDZeS1PGzwJJ5jGYVEqcXum7472kpUp")));
        assertEquals(programAddress2.getNonce(), 254);
    }

}
