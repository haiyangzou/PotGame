package org.pot.common.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.TreeMap;

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

    public static TreeMap<String, String> getParameters(HttpServletRequest request) throws IOException {
        String body = new String(IOUtils.toByteArray(request.getReader(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        TreeMap<String, String> params;
        if (StringUtils.isBlank(body)) {
            params = new TreeMap<>();
        } else if (StringUtils.containsIgnoreCase(request.getContentType(), "application/x-www-form-urlencoded")) {
            params = new TreeMap<>(UrlUtil.getUrlParams(body));
        } else {
            params = new TreeMap<>(JsonUtil.parseHashMap(body, String.class, String.class));
        }
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            params.put(parameterName, StringUtils.defaultString(request.getParameter(parameterName)));
        }
        return params;
    }
}
