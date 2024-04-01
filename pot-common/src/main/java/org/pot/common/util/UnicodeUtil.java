package org.pot.common.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class UnicodeUtil {
    public static boolean isValidStr(String str) {
        if (str == null || str.isEmpty())
            return false;
        if (isUnicode0(str)) {
            if (isAsciiCtrl(str))
                return false;
            if (isIso8859nCtrl(str))
                return false;
            if (isDelCtrl(str))
                return false;
            if (isPrivateUseZone(str))
                return false;
            if (isUTF16(str))
                return false;
            return !isUTF8mb4(str);
        } else {
            return false;
        }
    }

    private static boolean isUTF8mb4(String str) {
        int ih = 240;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            int ib = ih & b;
            if (ib == ih)
                return true;
        }
        return false;
    }

    private static final Pattern Ascii = Pattern.compile("^[\\x20-\\x7E]+$");

    public static boolean isAsciiCtrl(String str) {
        return Ascii.matcher(str).find();
    }

    private static final Pattern Iso8859n = Pattern.compile("[\\x00-\\x7F]");

    public static boolean isIso8859nCtrl(String str) {
        return Iso8859n.matcher(str).find();
    }

    private static final Pattern Ascii_Del = Pattern.compile("[DEL]");

    public static boolean isDelCtrl(String str) {
        return Ascii_Del.matcher(str).find();
    }

    private static final Pattern PrivateUseZone = Pattern.compile("\\A[^\\uE000-\\uFDCF]\\z");

    public static boolean isPrivateUseZone(String str) {
        return PrivateUseZone.matcher(str).find();
    }

    private static final Pattern UTF16 = Pattern.compile("\\A[\\u0000-\\uFFFF]*[\\u0000-\\uFFFF]\\z");

    public static boolean isUTF16(String str) {
        return UTF16.matcher(str).find();
    }

    private static final Pattern Unicode0 = Pattern.compile("\\A[^\\uD800-\\uDBFF]\\z");

    public static boolean isUnicode0(String str) {
        return Unicode0.matcher(str).find();
    }
}
