package org.pot.common.compressor;

import org.apache.commons.lang3.ArrayUtils;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class SnappyCompressor extends Compressor {

    @Override
    public byte[] compress(byte[] bytes) throws IOException, DataFormatException {
        if (ArrayUtils.isEmpty(bytes)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return Snappy.compress(bytes);
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException, DataFormatException {
        if (ArrayUtils.isEmpty(bytes)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return Snappy.uncompress(bytes);
    }
}
