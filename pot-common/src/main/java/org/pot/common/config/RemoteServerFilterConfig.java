package org.pot.common.config;

import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerType;
import org.pot.common.util.CollectionUtil;

import java.util.*;
import java.util.stream.Collectors;

public class RemoteServerFilterConfig {
    private static Map<ServerType, Set<Integer>> serverType2connectServerIds = new HashMap<>();
    private static Map<ServerType, Set<Integer>> serverType2excludeConnectionServerIds = new HashMap<>();

    private static boolean canConnectServer(Server server) {
        ServerType serverType = ServerType.valueOf(server.getTypeId());
        Set<Integer> excludeServerIds = serverType2excludeConnectionServerIds.get(serverType);
        if (CollectionUtil.isNotEmpty(excludeServerIds) && excludeServerIds.contains(server.getServerId())) {
            return false;
        }
        Set<Integer> serverIds = serverType2connectServerIds.get(serverType);
        return CollectionUtil.isEmpty(serverIds) || serverIds.contains(server.getServerId());
    }

    public static List<Server> filterConnectionServer(Collection<Server> serverList) {
        return serverList.stream().filter(RemoteServerFilterConfig::canConnectServer).collect(Collectors.toList());
    }
}
