package org.pot.common.compressor;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor extends Compressor {
    protected static final ThreadLocal<CompressorRatio> RATIO = ThreadLocal.withInitial(CompressorRatio::new);

    @Override
    public byte[] compress(byte[] bytes) throws IOException, DataFormatException {
        if (ArrayUtils.isEmpty(bytes)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        int compressedBytesLength = RATIO.get().getExpectedCompressedBytesLength(bytes.length);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(compressedBytesLength)) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream, BYTE_BUFFER_LENGTH)) {
                gzipOutputStream.write(bytes);
            }
            byte[] compressBytes = byteArrayOutputStream.toByteArray();
            RATIO.get().sumBytesLength(bytes.length, compressBytes.length);
            return compressBytes;
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException, DataFormatException {
        if (ArrayUtils.isEmpty(bytes)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        int decompressedBytesLength = RATIO.get().getExpectedDecompressedBytesLength(bytes.length);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(decompressedBytesLength)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes), BYTE_BUFFER_LENGTH)) {
                int readLength;
                byte[] buffer = BYTE_BUFFER.get();
                while ((readLength = gzipInputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, readLength);
                }
            }
            return byteArrayOutputStream.toByteArray();
        }

    }
}
