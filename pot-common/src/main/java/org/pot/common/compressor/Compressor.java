package org.pot.common.compressor;

import java.io.IOException;
import java.util.zip.DataFormatException;

public abstract class Compressor {
    protected static final int BYTE_BUFFER_LENGTH = 4096;
    protected static final ThreadLocal<byte[]> BYTE_BUFFER = ThreadLocal.withInitial(() -> new byte[BYTE_BUFFER_LENGTH]);

    public abstract byte[] compress(final byte[] bytes) throws IOException, DataFormatException;

    public abstract byte[] decompress(final byte[] bytes) throws IOException, DataFormatException;
}
