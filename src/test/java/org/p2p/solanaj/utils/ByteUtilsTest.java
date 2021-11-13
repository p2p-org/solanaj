package org.p2p.solanaj.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class ByteUtilsTest {

    @Test
    void readBytes() {
        byte[] buf = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, ByteUtils.readBytes(buf, 0, 4));
        assertArrayEquals(new byte[] { 5, 6, 7, 8, 9, 10, 11, 12 }, ByteUtils.readBytes(buf, 4, 8));
    }

    @Test
    void readUint64() throws IOException {
        String bigIntValue = "96351551052682965";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteUtils.uint64ToByteStreamLE(new BigInteger(bigIntValue), bos);

        BigInteger bn = ByteUtils.readUint64(bos.toByteArray(), 0);

        assertEquals(bigIntValue, bn.toString());
    }

    @Test
    void uint64ToByteStreamLE() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        assertThrows(RuntimeException.class,
                () -> ByteUtils.uint64ToByteStreamLE(new BigInteger("137001898677442802701"), bos));

    }

}
