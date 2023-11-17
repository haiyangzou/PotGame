package org.pot.common;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.FilenameUtil;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Constants {
    @Slf4j
    final class Env {
        @Getter
        private static List<String> localhostIp;//當前機器的IP地址
        @Getter
        private static List<String> wideNetIp;
        @Getter
        private static List<String> localNetIp;
        @Getter
        private static String contextPath;
        @Getter
        private static boolean debug;
        @Getter
        private static boolean web;

        static {
            try {
                log.info("TimeZone is{}", ZoneId.systemDefault());
//                localhostIp = ImmutableList.copyOf(Ipv4Util.getLocalhostIpv4Address(false));
                localhostIp = new ArrayList<>();
                localhostIp.add("172.16.30.61");
                if (CollectionUtil.isEmpty(localhostIp)) {
                    throw new IllegalStateException("Failed to Get Ipv4 Address");
                }
                wideNetIp = ImmutableList.copyOf(localhostIp.stream().filter(Ipv4Util::isWideNetworkIpv4Address).collect(Collectors.toList()));
                localNetIp = ImmutableList.copyOf(localhostIp.stream().filter(Ipv4Util::isWideNetworkIpv4Address).collect(Collectors.toList()));
                log.info("LocalhostIp is {}", Ipv4Util.join(localhostIp));
                File userDir = new File(System.getProperty("user.dir"));
                if (userDir.exists()) {
                    contextPath = FilenameUtil.formatPath(userDir);
                } else {
                    contextPath = StringUtils.EMPTY;
                }
                log.info("ContextPath is{}", contextPath);

            } catch (Throwable throwable) {
                throw new RuntimeException("Constants Env Error", throwable);
            }
        }

        public static void setDebugOption(String debugOption) {
            debug = BooleanUtils.toBoolean(debugOption);
            if (debug) {
                log.warn("DebugOption={},please ensure this is not production environment!", debugOption);
            }
        }

        public static void setWebOption(String webOption) {
            web = BooleanUtils.toBoolean(webOption);
            if (web) {
                log.warn("WebOption={},please ensure this is not production environment!", webOption);
            }
        }
    }

    long AWAIT_MS = 10L;
    long AWAIT_TIMEOUT_MS = 10 * 1000L;
    long RUN_INTERVAL_MS = 10L;
    long RUN_SLOW_MS = 10L;
    long NET_SLOW_MS = 500L;
    String WEB_STATIC = "web-static";
}
