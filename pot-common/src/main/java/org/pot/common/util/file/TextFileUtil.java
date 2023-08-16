package org.pot.common.util.file;

import io.netty.util.CharsetUtil;
import org.apache.commons.io.FileUtils;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.GzipCompressor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public class TextFileUtil {
    private TextFileUtil() {
    }

    public static String read(String file) throws IOException {
        return read(file, false);
    }

    public static String read(File file) throws IOException {
        return read(file, false);
    }

    public static String read(File file, boolean useGzip) throws IOException {
        return read(file, CharsetUtil.UTF_8, false);
    }

    public static String read(File file, Charset charset, boolean useGzip) throws IOException {
        return read(file, charset, useGzip ? new GzipCompressor() : null);
    }

    public static String read(File file, Charset charset, Compressor compressor) throws IOException {
        return new String(prepareReadBytes(file, compressor), charset);
    }

    private static byte[] prepareReadBytes(File file, Compressor compressor) throws IOException {
        Objects.requireNonNull(file);
        byte[] bytes = FileUtils.readFileToByteArray(file);
        if (compressor != null) {
            try {
                bytes = compressor.decompress(bytes);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return bytes;

    }

    public static String read(String file, boolean useGzip) throws IOException {
        return read(file, false);
    }
}
