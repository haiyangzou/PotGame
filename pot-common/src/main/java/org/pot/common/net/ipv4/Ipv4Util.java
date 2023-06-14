package org.pot.common.net.ipv4;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Ipv4Util {
    /**
     * 通过掩码位数,获取如这样表示的ipv4地址"255.255.255.255"的掩码的字符串
     * 8 - 255.0.0.0
     * 16 - 255.255.0.0
     * 24 - 255.255.255.0
     * 32 - 255.255.255.255
     */

    public static String toMaskString(int maskBitLength) {
        if (maskBitLength < 0 || maskBitLength > 32) {
            throw new IllegalArgumentException("Invalid mask bit length: " + maskBitLength);
        }
        int mask = 0xffffffff << (32 - maskBitLength);
        return String.format("%d.%d.%d.%d", (mask >> 24) & 0xff, (mask >> 16) & 0xff, (mask >> 8) & 0xff, mask & 0xff);
    }

    /**
     * Ipv4的正则表达式，用于判断Ipv4地址是否合法
     */

    private static final Pattern IPV4_REGEX = Pattern
            .compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    public static boolean isIpv4Address(String ipString) {
        return StringUtils.isNotBlank(ipString) && IPV4_REGEX.matcher(ipString).matches();
    }

    public static boolean isWrongIpv4Address(String ipString) {
        return !isIpv4Address(ipString);
    }

    /**
     * 判断Ipv4地址是否合法
     */
    public static void ensureIpv4Valid(String ipv4) {
        if (isWrongIpv4Address(ipv4)) {
            throw new IllegalArgumentException("Error ipv4 address:" + ipv4);
        }
    }

    /**
     * 获取ip值与mask值'与'的结果
     * 
     */

    public static long toNetSegmentValue(String ipString, String maskString) {
        return toNetSegmentValue(ipString, toIpv4Value(maskString));
    }

    public static long toNetSegmentValue(String ipString, long mask) {
        return (toIpv4Value(ipString) & mask);
    }

    /**
     * 将ip字符串转换为32bit值
     */

    public static long toIpv4Value(String ipString) {
        long result = 0;
        short[] bytes = toIpv4UnsignedBytes(ipString);
        for (int i = 0; i < 4; i++) {
            result |= (bytes[i] & 0xFFL) << (24 - (i * 8));
        }
        return result;
    }

    /**
     * 将ip字符串转换为无符号字节数组
     */

    public static short[] toIpv4UnsignedBytes(String ipString) {
        ensureIpv4Valid(ipString);
        String[] ipArray = ipString.split("\\.");
        short[] result = new short[4];
        for (int i = 0; i < 4; i++) {
            result[i] = Short.parseShort(ipArray[i]);
        }
        return result;
    }

    /**
     * 是否是回环地址
     */

    public static boolean isLoopbackIpv4Address(String ipv4) {
        return StringUtils.equals("127.0.0.1", StringUtils.strip(ipv4));
    }

}
