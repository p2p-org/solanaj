package org.p2p.solanaj.utils;

import static org.bitcoinj.core.Utils.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class ByteUtils {
    public static final int UINT_32_LENGTH = 4;
    public static final int UINT_64_LENGTH = 8;

    public static byte[] readBytes(byte[] buf, int offset, int length) {
        byte[] b = new byte[length];
        System.arraycopy(buf, offset, b, 0, length);
        return b;
    }

    public static BigInteger readUint64(byte[] buf, int offset) {
        return new BigInteger(reverseBytes(readBytes(buf, offset, UINT_64_LENGTH)));
    }

    public static BigInteger readUint64Price(byte[] buf, int offset) {
        return new BigInteger(readBytes(buf, offset, UINT_64_LENGTH));
    }

    public static void uint64ToByteStreamLE(BigInteger val, OutputStream stream) throws IOException {
        byte[] bytes = val.toByteArray();
        if (bytes.length > 8) {
            if (bytes[0] == 0) {
                bytes = readBytes(bytes, 1, bytes.length - 1);
            } else {
                throw new RuntimeException("Input too large to encode into a uint64");
            }
        }
        bytes = reverseBytes(bytes);
        stream.write(bytes);
        if (bytes.length < 8) {
            for (int i = 0; i < 8 - bytes.length; i++)
                stream.write(0);
        }
    }

}
