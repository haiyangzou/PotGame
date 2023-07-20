package org.pot.common.compressor;

import java.io.IOException;
import java.util.zip.DataFormatException;

public abstract class Compressor {
    public abstract byte[] compress(final byte[] bytes) throws IOException, DataFormatException;

    public abstract byte[] decompress(final byte[] bytes) throws IOException, DataFormatException;
}
