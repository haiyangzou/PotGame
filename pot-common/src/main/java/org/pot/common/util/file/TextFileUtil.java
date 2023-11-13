package org.pot.common.util.file;

import org.apache.commons.io.FileUtils;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.GzipCompressor;
import org.pot.common.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.zip.DataFormatException;

public class TextFileUtil {
    private TextFileUtil() {
    }

    public static void write(String content, String filePath, boolean append, boolean useGzip) throws IOException {
        write(content, filePath, CharsetUtil.defaultCharset(), append, useGzip);
    }

    public static void write(String content, String filePath, Charset charset, boolean append, boolean useGzip) throws IOException {
        write(content, new File(filePath), charset, append, useGzip);
    }

    public static void write(String content, File file, Charset charset, boolean append, boolean useGzip) throws IOException {
        write(content, file, charset, append, useGzip ? new GzipCompressor() : null);
    }

    public static void write(String content, File file, Charset charset, boolean append, Compressor compressor) throws IOException {
        byte[] bytes = prepareWriteBytes(content, charset, compressor);
        writeToFile(file, append, bytes);
    }

    private static byte[] prepareWriteBytes(String content, Charset charset, Compressor compressor) throws IOException {
        Charset useCharset = CharsetUtil.nullToDefaultCharset(charset);
        byte[] bytes = content.getBytes(useCharset);
        if (compressor != null) {
            try {
                bytes = compressor.compress(bytes);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static void writeToFile(File file, boolean append, byte[] bytes) throws IOException {
        FileUtil.createFileOnNoExists(file);
        FileUtil.write(file, bytes, append);
    }

    public static String read(String file) throws IOException {
        return read(file, false);
    }

    public static String read(File file) throws IOException {
        return read(file, false);
    }

    public static String read(File file, boolean useGzip) throws IOException {
        return read(file, CharsetUtil.defaultCharset(), false);
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
