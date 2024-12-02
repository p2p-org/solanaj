package org.p2p.solanaj.core;

import java.util.Arrays;

public class Binary {

    public static byte[] uint32(long value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte) (0xFFL & value);
        byteArray[1] = (byte) (0xFFL & (value >> 8));
        byteArray[2] = (byte) (0xFFL & (value >> 16));
        byteArray[3] = (byte) (0xFFL & (value >> 24));
        return byteArray;
    }

    public static byte[] int64(long value) {
        byte[] byteArray = new byte[8];
        byteArray[0] = (byte) (0xFFL & value);
        byteArray[1] = (byte) (0xFFL & (value >> 8));
        byteArray[2] = (byte) (0xFFL & (value >> 16));
        byteArray[3] = (byte) (0xFFL & (value >> 24));
        byteArray[4] = (byte) (0xFFL & (value >> 32));
        byteArray[5] = (byte) (0xFFL & (value >> 40));
        byteArray[6] = (byte) (0xFFL & (value >> 48));
        byteArray[7] = (byte) (0xFFL & (value >> 56));
        return byteArray;
    }

    public static byte[] uint16(long value) {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) (0xFFL & value);
        byteArray[1] = (byte) (0xFFL & (value >> 8));
        return byteArray;
    }

    public static byte[] encodeLength(int len) {
        byte[] out = new byte[10];
        int remLen = len;
        int cursor = 0;
        while (true) {
            int elem = remLen & 0x7f;
            remLen = remLen >> 7;
            if (remLen == 0) {
                byte[] uint16 = uint16(elem);
                out[cursor] = uint16[0];
                out[cursor + 1] = uint16[1];
                break;
            } else {
                elem = elem | 0x80;
                byte[] uint16 = uint16(elem);
                out[cursor] = uint16[0];
                out[cursor + 1] = uint16[1];
                cursor += 1;
            }
        }
        byte[] bytes = new byte[cursor + 1];
        System.arraycopy(out, 0, bytes, 0, cursor + 1);
        return bytes;
    }

    public static DecodedLength decodeLength(byte[] bytes) {
        byte[] newBytes = bytes;
        int len = 0;
        int size = 0;
        while (true) {
            int elem = newBytes[0];
            newBytes = Arrays.copyOfRange(newBytes, 1, newBytes.length);

            len = len | ((elem & 0x7f) << (size * 7));
            size += 1;
            if ((elem & 0x80) == 0) {
                break;
            }
        }
        return new DecodedLength(len, newBytes);
    }

    public static class DecodedLength {

        public int length;
        public byte[] bytes;

        public DecodedLength(int length, byte[] bytes) {
            this.length = length;
            this.bytes = bytes;
        }

        public boolean equals(DecodedLength other) {

            if (this == other) {
                return true;
            }

            if (length != other.length) {
                return false;
            }
            if (!Arrays.equals(bytes, other.bytes)) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result = length;
            result = 31 * result + Arrays.hashCode(bytes);
            return result;
        }
    }
}
