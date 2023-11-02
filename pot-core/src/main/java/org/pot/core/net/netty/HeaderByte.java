package org.pot.core.net.netty;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeaderByte {
    private static final byte COMPRESSED = 1;
    private static final byte ENCRYPTED = 1 << 1;
    private static final byte TEXT = 1 << 2;
    @Getter
    private final boolean compressed;
    @Getter
    private final boolean encrypted;
    @Getter
    private final boolean text;

    public HeaderByte(boolean compressed, boolean encrypted, boolean text) {
        this.compressed = compressed;
        this.encrypted = encrypted;
        this.text = text;
    }

    public static boolean isCompressed(byte headerByte) {
        return (headerByte & COMPRESSED) > 0;
    }

    public static byte toHeadByte(boolean compressed) {
        return toHeadByte(compressed, false, false);
    }

    public static byte toHeadByte(boolean compressed, boolean encrypted, boolean text) {
        byte headerByte = 0;
        if (compressed) {
            headerByte |= COMPRESSED;
        }
        if (encrypted) {
            headerByte |= ENCRYPTED;
        }
        if (text) {
            headerByte |= TEXT;
        }
        return headerByte;
    }
}
