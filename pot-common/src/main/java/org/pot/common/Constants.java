package org.pot.common;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.util.CollectionUtil;

import java.time.ZoneId;
import java.util.List;

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

        static {
            try {
                log.info("TimeZone is{}", ZoneId.systemDefault());
                localhostIp = ImmutableList.copyOf(Ipv4Util.getLocalhostIpv4Address(false));
                if (CollectionUtil.isEmpty(localhostIp)) {

                }
            } catch (Throwable throwable) {
                throw new RuntimeException("Constants Env Error", throwable);
            }
        }
    }


    long NET_SLOW_MS = 500L;
}
