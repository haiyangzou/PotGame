package org.pot.common.net.ipv4;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Ipv4Util {
    public static List<String> getLocalhostIpv4Address(boolean containsLoopback) throws SocketException {
        List<Pair<String, String>> ipv4AddressAndMask = getLocalhostIpv4AddressAndMask(containsLoopback);
        return ipv4AddressAndMask.stream().map(Pair::getLeft).collect(Collectors.toList());
    }

    public static List<Pair<String, String>> getLocalhostIpv4AddressAndMask(boolean containsLoopback) throws SocketException {
        List<Pair<String, String>> ipv4 = new ArrayList<>();
        return ipv4;
    }

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

    public enum Ipv4Type {

        IPV4_A_LOCAL("10.0.0.0", "10.255.255.255", "255.0.0.0"),
        IPV4_A_LOCALHOST("127.0.0.1", "127.0.0.1", "255.255.255.255"),
        IPV4_A("0.0.0.0", "127.255.255.255", "255.0.0.0"),
        IPV4_B_LOCAL("172.16.0.0", "172.31.255.255", "255.0.0.0"),
        IPV4_B("128.0.0.0", "191.255.255.255", "255.255.0.0"),
        IPV4_C_LOCAL("192.168.0.0", "192.168.255.255", "255.255.255.0"),
        IPV4_C("192.0.0.0", "223.255.255.255", "255.255.255.0"),
        IPV4_D("224.0.0.0", "239.255.255.255", "255.255.255.255"),
        IPV4_E("240.0.0.0", "255.255.255.255", "255.255.255.255"),
        ;
        private final long mask;
        private final Range<Long> range;

        Ipv4Type(String headString, String tailString, String maskString) {
            mask = toIpv4Value(maskString);
            range = Range.between(toIpv4Value(headString), toIpv4Value(tailString));
        }

        public boolean contains(long ipValue) {
            return range.contains(ipValue);
        }
    }

    /**
     * Ipv4的正则表达式，用于判断Ipv4地址是否合法
     */

    private static final Pattern IPV4_REGEX = Pattern
            .compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    public static boolean isIpv4Address(String ipString) {
        return StringUtils.isNotBlank(ipString) && IPV4_REGEX.matcher(ipString).matches();
    }

    public static boolean isWideNetworkIpv4Address(String ipv4) {
        return noneMatch(ipv4, Ipv4Type.IPV4_A_LOCAL, Ipv4Type.IPV4_A_LOCALHOST, Ipv4Type.IPV4_B_LOCAL, Ipv4Type.IPV4_C_LOCAL);
    }

    public static boolean isLocalNetworkIpv4Address(String ipv4) {
        return noneMatch(ipv4, Ipv4Type.IPV4_A, Ipv4Type.IPV4_B, Ipv4Type.IPV4_C, Ipv4Type.IPV4_D, Ipv4Type.IPV4_E);
    }

    public static Ipv4Type getIpv4Type(long ipValue) {
        for (Ipv4Type ipv4Type : Ipv4Type.values()) {
            if (ipv4Type.contains(ipValue)) {
                return ipv4Type;
            }
        }
        return null;
    }

    public static Ipv4Type getIpv4Type(String ipString) {
        return getIpv4Type(toIpv4Value(ipString));
    }

    public static boolean noneMatch(String ipString, Ipv4Type... types) {
        Ipv4Type ipv4Type = getIpv4Type(ipString);
        return Arrays.stream(types).noneMatch(t -> t == ipv4Type);
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

    public static String join(List<String> multiIpv4) {
        return StringUtils.trimToEmpty(StringUtils.join(multiIpv4, ","));
    }

}
