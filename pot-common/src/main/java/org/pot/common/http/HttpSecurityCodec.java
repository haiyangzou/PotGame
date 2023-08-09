package org.pot.common.http;

import com.google.common.io.BaseEncoding;
import org.pot.common.cipher.SecurityUtil;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;

public class HttpSecurityCodec {
    private static final Compressor compressor = new SnappyCompressor();

    public static byte[] decode(String data) throws Exception {
        byte[] bytes = BaseEncoding.base64().decode(data);
        bytes = SecurityUtil.decryptRsa(bytes);
        bytes = compressor.decompress(bytes);
        return bytes;
    }
}