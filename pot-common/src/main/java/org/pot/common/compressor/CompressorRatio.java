package org.pot.common.compressor;

import org.pot.common.util.MathUtil;

public class CompressorRatio {
    private long beforeCompressBytes = 0;
    private long afterCompressBytes = 0;

    double getCompressedRatio() {
        if (beforeCompressBytes <= 0 || afterCompressBytes <= 0) {
            return 0.5D;
        }
        double ratio = (afterCompressBytes * 1D) / (beforeCompressBytes * 1D);
        return Double.isNaN(ratio) || ratio <= 0D || ratio >= 1D ? 0.5D : ratio;
    }

    void sumBytesLength(int beforeCompressBytes, int afterCompressBytes) {
        this.beforeCompressBytes += beforeCompressBytes;
        this.afterCompressBytes += afterCompressBytes;
        if (beforeCompressBytes <= 0 || afterCompressBytes <= 0) {
            this.beforeCompressBytes = 0;
            this.afterCompressBytes = 0;
        }
    }

    int getExpectedCompressedBytesLength(int uncompressedBytes) {
        int size = 1 + (int) (uncompressedBytes * getCompressedRatio());
        return Compressor.BYTE_BUFFER_LENGTH * MathUtil.divideAndCeil(size, Compressor.BYTE_BUFFER_LENGTH);
    }

    int getExpectedDecompressedBytesLength(int compressedBytes) {
        int size = 1 + (int) (compressedBytes * getCompressedRatio());
        return Compressor.BYTE_BUFFER_LENGTH * MathUtil.divideAndCeil(size, Compressor.BYTE_BUFFER_LENGTH);
    }
}
