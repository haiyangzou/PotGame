package org.pot.common.binary;

import javax.xml.bind.DatatypeConverter;

public class HexUtil {
    public static String encode(final byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static byte[] decode(final String hex) {
        return DatatypeConverter.parseHexBinary(hex);
    }
}
