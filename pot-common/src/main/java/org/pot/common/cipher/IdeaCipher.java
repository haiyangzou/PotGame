package org.pot.common.cipher;

import org.apache.commons.lang3.ArrayUtils;
import org.pot.common.binary.HexUtil;

import java.nio.charset.StandardCharsets;

public class IdeaCipher {
    private static final int FLAG = 0x10001;
    private static final int BYTES_SECRET_KEY_SIZE = 16;
    private static final int UNIT_BYTES_LENGTH = 8;
    private static final int SHORTS_SECRET_KEY_SIZE = 8;
    private static final int ENCRYPTION_CYCLE = 8;
    private static final int SECRET_SUB_KEY_COUNT = 52;

    public static String encrypt(final byte[] key, final String data) {
        final byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = encrypt(key, dataBytes);
        return HexUtil.encode(bytes);
    }

    public static byte[] encrypt(final byte[] key, final byte[] data) {
        checkKey(key);
        final int padding = UNIT_BYTES_LENGTH - (data.length % UNIT_BYTES_LENGTH);
        final byte[] outBytes = new byte[data.length + padding];
        System.arraycopy(data, 0, outBytes, 0, data.length);
        outBytes[outBytes.length - 1] = (byte) padding;
        final byte[] block = new byte[UNIT_BYTES_LENGTH];
        for (int time = 0; time < outBytes.length / UNIT_BYTES_LENGTH; time++) {
            int position = time * UNIT_BYTES_LENGTH;
            System.arraycopy(outBytes, position, block, 0, UNIT_BYTES_LENGTH);
            System.arraycopy(cipher(key, block, true), 0, outBytes, position, UNIT_BYTES_LENGTH);
        }
        return outBytes;
    }

    private static int bytesToUnsignedShort(final byte[] bytes, final int position) {
        return ((bytes[position] << 8) & 0xFF00) + (bytes[position + 1] & 0xFF);
    }

    private static int[] getEncryptSubKeys(final byte[] keyBytes) {
        int[] encryptSubKeys = new int[SECRET_SUB_KEY_COUNT];
        for (int i = 0; i < SHORTS_SECRET_KEY_SIZE; i++) {
            encryptSubKeys[i] = bytesToUnsignedShort(keyBytes, i * 2);
        }
        for (int i = 8; i < encryptSubKeys.length; i++) {
            if ((i & 0x7) < 6) {
                encryptSubKeys[i] = (((encryptSubKeys[i - 7] & 0x7f) << 9) | (encryptSubKeys[i - 6] >> 7)) & 0xFFFF;
            } else if ((i & 0x7) == 6) {
                encryptSubKeys[i] = (((encryptSubKeys[i - 7] & 0x7f) << 9) | (encryptSubKeys[i - 14] >> 7)) & 0xFFFF;
            } else {
                encryptSubKeys[i] = (((encryptSubKeys[i - 15] & 0x7f) << 9) | (encryptSubKeys[i - 14] >> 7)) & 0xFFFF;
            }
        }
        return encryptSubKeys;
    }

    private static int mul(int a, int b) {
        if (a == 0) {
            a = FLAG - b;
        } else if (b == 0) {
            a = FLAG - a;
        } else {
            int tmp = a * b;
            b = tmp & 0xFFFF;
            a = tmp >>> 16;
            a = (b - a) + (b < a ? 1 : 0);
        }
        return a & 0xFFFF;
    }

    private static int invMul(int a) {
        if (a <= 1) {
            return a;
        }
        int b = 1;
        int c = FLAG / a;
        for (int i = FLAG % a; i != 1; ) {
            int d = a / i;
            a %= i;
            b = (b + (c * d)) & 0xFFFF;
            if (a == 1) {
                return b;
            }
            d = i / a;
            i %= a;
            c = (c + (b * d)) & 0xFFFF;
        }
        return (1 - c) & 0xFFFF;
    }

    private static int[] getDecryptSubKeys(final int[] encryptSubKeys) {
        int head = 0, tail = encryptSubKeys.length;
        int[] decryptSubKeys = new int[encryptSubKeys.length];
        int invA = invMul(encryptSubKeys[head++]);
        int invB = (0 - encryptSubKeys[head++]) & 0xFFFF;
        int invC = (0 - encryptSubKeys[head++]) & 0xFFFF;
        int invD = invMul(encryptSubKeys[head++]);
        decryptSubKeys[--tail] = invD;
        decryptSubKeys[--tail] = invC;
        decryptSubKeys[--tail] = invB;
        decryptSubKeys[--tail] = invA;
        for (int i = 0; i < ENCRYPTION_CYCLE; i++) {
            invA = encryptSubKeys[head++];
            invB = encryptSubKeys[head++];
            decryptSubKeys[--tail] = invB;
            decryptSubKeys[--tail] = invA;
            invA = invMul(encryptSubKeys[head++]);
            invB = (0 - encryptSubKeys[head++]) & 0xFFFF;
            invC = (0 - encryptSubKeys[head++]) & 0xFFFF;
            invD = invMul(encryptSubKeys[head++]);
            decryptSubKeys[--tail] = invD;
            decryptSubKeys[--tail] = invC;
            decryptSubKeys[--tail] = invB;
            decryptSubKeys[--tail] = invA;
        }
        invA = encryptSubKeys[head++];
        invB = encryptSubKeys[head++];
        decryptSubKeys[--tail] = invB;
        decryptSubKeys[--tail] = invA;
        invA = invMul(encryptSubKeys[head++]);
        invB = (0 - encryptSubKeys[head++]) & 0xFFFF;
        invC = (0 - encryptSubKeys[head++]) & 0xFFFF;
        invD = invMul(encryptSubKeys[head++]);
        decryptSubKeys[--tail] = invD;
        decryptSubKeys[--tail] = invC;
        decryptSubKeys[--tail] = invB;
        decryptSubKeys[--tail] = invA;
        return decryptSubKeys;
    }

    private static int[] getSubKeys(final boolean type, final byte[] keyBytes) {
        return type ? getEncryptSubKeys(keyBytes) : getDecryptSubKeys(getEncryptSubKeys(keyBytes));
    }


    private static byte[] cipher(final byte[] keyBytes, final byte[] dataBytes, final boolean type) {
        final byte[] outBytes = new byte[UNIT_BYTES_LENGTH];
        cipher(getSubKeys(type, keyBytes), dataBytes, outBytes);
        return outBytes;
    }

    private static void cipher(final int[] subKeys, final byte[] dataBytes, final byte[] outBytes) {
        int keyIndex = 0;
        int a = bytesToUnsignedShort(dataBytes, 0);
        int b = bytesToUnsignedShort(dataBytes, 2);
        int c = bytesToUnsignedShort(dataBytes, 4);
        int d = bytesToUnsignedShort(dataBytes, 6);
        for (int i = 0; i < ENCRYPTION_CYCLE; i++) {
            a = mul(a, subKeys[keyIndex++]);
            b = (b + subKeys[keyIndex++]) & 0xFFFF;
            c = (c + subKeys[keyIndex++]) & 0xFFFF;
            d = mul(d, subKeys[keyIndex++]);
            int temp1 = b;
            int temp2 = c;
            c = c ^ a;
            b = b ^ d;
            c = mul(c, subKeys[keyIndex++]);
            b = (b + c) & 0xFFFF;
            b = mul(b, subKeys[keyIndex++]);
            c = (c + b) & 0xFFFF;
            a = a ^ b;
            d = d ^ c;
            b = b ^ temp2;
            c = c ^ temp1;
        }
        unsignedShortToBytes(mul(a, subKeys[keyIndex++]), outBytes, 0);
        unsignedShortToBytes((c + subKeys[keyIndex++]) & 0xFFFF, outBytes, 2);
        unsignedShortToBytes((b + subKeys[keyIndex++]) & 0xFFFF, outBytes, 4);
        unsignedShortToBytes(mul(d, subKeys[keyIndex++]), outBytes, 6);
    }

    private static void unsignedShortToBytes(final int val, final byte[] bytes, final int position) {
        bytes[position] = (byte) (val >>> 8);
        bytes[position + 1] = (byte) val;
    }

    private static void checkKey(final byte[] key) {
        if (ArrayUtils.isEmpty(key)) {
            throw new IllegalArgumentException("empty key for IDEA cipher");
        }
        if (key.length != BYTES_SECRET_KEY_SIZE) {
            throw new IllegalArgumentException("Wrong key size");
        }
    }

    public static String decrypt(final byte[] key, final String data) {
        final byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = encrypt(key, dataBytes);
        return HexUtil.encode(bytes);
    }

}
