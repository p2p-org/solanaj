package org.p2p.solanaj.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class PublicKeyTest {

    @Test(expected = IllegalArgumentException.class)
    public void ivalidKeys() {
        new PublicKey(new byte[] { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0 });
        new PublicKey("300000000000000000000000000000000000000000000000000000000000000000000");
        new PublicKey("300000000000000000000000000000000000000000000000000000000000000");
    }

    @Test
    public void validKeys() {
        PublicKey key = new PublicKey(new byte[] { 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, });
        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", key.toString());

        PublicKey key1 = new PublicKey("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3");
        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", key1.toBase58());

        PublicKey key2 = new PublicKey("11111111111111111111111111111111");
        assertEquals("11111111111111111111111111111111", key2.toBase58());

        byte[] byteKey = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, };
        PublicKey key3 = new PublicKey(byteKey);
        assertArrayEquals(byteKey, new PublicKey(key3.toBase58()).toByteArray());
    }

    @Test
    public void equals() {
        PublicKey key = new PublicKey("11111111111111111111111111111111");
        assertTrue(key.equals(key));

        assertFalse(key.equals(new PublicKey("11111111111111111111111111111112")));
    }
}
