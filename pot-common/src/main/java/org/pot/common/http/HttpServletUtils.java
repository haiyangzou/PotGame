package org.pot.common.http;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.net.ipv4.Ipv4Util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletUtils {
    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("X-Forwarded");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getRemoteAddr();
        }
        for (String ipv4 : Ipv4Util.split(ip, Ipv4Util::isIpv4Address)) {
            return ipv4;
        }
        return ip;
    }
}
