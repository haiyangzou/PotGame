package org.pot.core.net.netty;

import org.apache.commons.lang3.ArrayUtils;

public class FrameCipher {
    private final int initialSndKey;
    private final int initialRcvKey;
    private int sndKey = 0;
    private int rcvKey = 0;

    public FrameCipher(final int initialKey) {
        this(initialKey, initialKey);
    }

    public FrameCipher(final int initialSndKey, final int initialRcvKey) {
        this.initialSndKey = initialSndKey;
        this.initialRcvKey = initialRcvKey;
    }

    byte encrypt(byte[] bytes) {
        if (++sndKey == initialSndKey) {
            sndKey = 0;
        }
        if (ArrayUtils.isEmpty(bytes)) {
            return 0;
        }
        int sum = 0;
        int encryptKey = (~(sndKey % 0x100)) & 0xFF;
        for (int i = 0; i < bytes.length; i++) {
            sum += (bytes[i] & 0xFF);
            bytes[i] = encryptByte(bytes[i], encryptKey);
        }
        byte summary = (byte) ((sum % 0x100) & 0xFF);
        return encryptByte(summary, encryptKey);
    }

    boolean decrypt(byte check, byte[] bytes) {
        if (++rcvKey == initialRcvKey) {
            rcvKey = 0;
        }
        if (ArrayUtils.isEmpty(bytes)) {
            return true;
        }
        int sum = 0;
        int decryptKey = (~(rcvKey % 0x100)) & 0xFF;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = decryptByte(bytes[i], decryptKey);
            sum += (bytes[i] & 0xFF);
        }
        byte summary = (byte) ((sum % 0x100) & 0xFF);
        check = decryptByte(check, decryptKey);
        return summary == check;
    }

    private byte encryptByte(final byte aByte, final int key) {
        byte b = aByte;
        int i = b & 0xFF;
        i = (i + key) % 0x100;
        b = (byte) ((~(i & 0xFF)) & 0xFF);
        b = (byte) ((b ^ ((key << 4) | (key >>> 4))) & 0xFF);
        return b;
    }

    private byte decryptByte(final byte aByte, final int key) {
        byte b = aByte;
        b = (byte) ((b ^ ((key << 4) | (key >>> 4))) & 0xFF);
        b = (byte) (~(b) & 0xFF);
        int i = b & 0xFF;
        i = (i + 0x100 - key) % 0x100;
        return (byte) (i & 0xFF);
    }

    public int getSndKey() {
        return sndKey;
    }

    public int getRcvKey() {
        return rcvKey;
    }
}
