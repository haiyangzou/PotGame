package org.pot.common.binary;

import com.google.common.io.BaseEncoding;

public class Base64Util {
    public static byte[] decode(String base64String) {
        return BaseEncoding.base64().decode(base64String);
    }

    public static String encode(byte[] bytes) {
        return BaseEncoding.base16().encode(bytes);
    }
}
