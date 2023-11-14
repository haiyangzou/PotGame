package org.pot.common.http;

import com.google.common.io.BaseEncoding;
import org.pot.common.cipher.SecurityUtil;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;

import java.nio.charset.StandardCharsets;

public class HttpSecurityCodec {
    private static final Compressor compressor = new SnappyCompressor();

    public static String encode(byte[] data) throws Exception {
        byte[] bytes = compressor.compress(data);
        bytes = SecurityUtil.encryptRsa(bytes);
        return BaseEncoding.base64Url().encode(bytes);
    }

    public static String encodeString(String data) throws Exception {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return encode(bytes);
    }

    public static byte[] decode(String data) throws Exception {
        byte[] bytes = BaseEncoding.base64Url().decode(data);
        bytes = SecurityUtil.decryptRsa(bytes);
        bytes = compressor.decompress(bytes);
        return bytes;
    }

    public static String decodeString(String data) throws Exception {
        byte[] bytes = decode(data);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
