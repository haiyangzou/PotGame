package org.pot.common.compressor;


import org.pot.common.binary.Base64Util;

import java.io.IOException;
import java.util.zip.DataFormatException;

public abstract class Compressor {
    protected static final int BYTE_BUFFER_LENGTH = 4096;
    protected static final ThreadLocal<byte[]> BYTE_BUFFER = ThreadLocal.withInitial(() -> new byte[BYTE_BUFFER_LENGTH]);

    public String compressToBase64String(byte[] bytes) throws IOException, DataFormatException {
        final byte[] compressBytes = compress(bytes);
        return Base64Util.encode(compressBytes);

    }

    public byte[] decompressBase64StringToBytes(String string) throws IOException, DataFormatException {
        final byte[] compressBytes = Base64Util.decode(string);
        return decompress(compressBytes);
    }

    public abstract byte[] compress(final byte[] bytes) throws IOException, DataFormatException;

    public abstract byte[] decompress(final byte[] bytes) throws IOException, DataFormatException;
}
