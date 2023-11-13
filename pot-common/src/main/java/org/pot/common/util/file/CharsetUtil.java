package org.pot.common.util.file;

import java.nio.charset.Charset;

public class CharsetUtil {
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    public static Charset nullToDefaultCharset(Charset charset) {
        return charset == null ? defaultCharset() : charset;
    }

    public static Charset defaultCharset() {
        return CHARSET_UTF_8;
    }
}
