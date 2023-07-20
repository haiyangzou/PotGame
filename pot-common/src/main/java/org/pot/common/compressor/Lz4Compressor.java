package org.pot.common.compressor;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class Lz4Compressor extends Compressor {
    private final LZ4Compressor lz4FastCompressor;
    private final LZ4FastDecompressor lz4FastDecompressor;

    public Lz4Compressor() {
        LZ4Factory lz4Factory = LZ4Factory.safeInstance();
        lz4FastCompressor = lz4Factory.fastCompressor();
        lz4FastDecompressor = lz4Factory.fastDecompressor();
    }

    @Override
    public byte[] compress(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return new byte[0];
    }
}
