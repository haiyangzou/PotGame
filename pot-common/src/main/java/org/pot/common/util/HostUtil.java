package org.pot.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.net.ipv4.Ipv4Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

public class HostUtil {
    public static List<String> getHostAddressList(String host, boolean onlyIpv4) {
        try {
            InetAddress[] inetAddresses = InetAddress.getAllByName(host);
            Set<String> set = Sets.newHashSetWithExpectedSize(inetAddresses.length);
            for (InetAddress inetAddress : inetAddresses) {
                String address = StringUtils.strip(inetAddress.getHostAddress());
                if (StringUtils.isBlank(address)) {
                    continue;
                }
                if (onlyIpv4 && !Ipv4Util.isIpv4Address(address)) {
                    continue;
                }
                set.add(address);
            }
            return ImmutableList.copyOf(set);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
