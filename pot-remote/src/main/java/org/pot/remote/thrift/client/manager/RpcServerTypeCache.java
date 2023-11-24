package org.pot.remote.thrift.client.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.executor.ScheduledExcutor;
import org.pot.common.config.RemoteServerFilterConfig;
import org.pot.common.util.MapUtil;
import org.pot.common.util.RandomUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class RpcServerTypeCache {
    private final ServerType serverType;
    private final Map<Integer, RpcServerCache> serverIdMap = new ConcurrentHashMap<>();

    public RpcServerTypeCache(ServerType serverType) {
        this.serverType = serverType;
    }

    public void close() {
        for (RpcServerCache rpcServerCache : serverIdMap.values()) {
            rpcServerCache.close();
        }
    }

    void ensureAvailable(ScheduledExcutor executor, Collection<Server> serverList) {
        serverList = RemoteServerFilterConfig.filterConnectionServer(serverList);
        List<Server> typeServerList = serverList.stream().filter(server -> server.getTypeId() == serverType.getId()).collect(Collectors.toList());
        Map<Integer, Server> definedTypeServers = MapUtil.toHashMap(typeServerList, Server::getServerId, server -> server);
        Iterator<RpcServerCache> iterator = serverIdMap.values().iterator();
        while (iterator.hasNext()) {
            RpcServerCache rpcServerCache = iterator.next();
            if (!definedTypeServers.containsKey(rpcServerCache.getServer().getServerId())) {
                rpcServerCache.close();
                iterator.remove();
            }
        }
        for (Server server : typeServerList) {
            int serverId = server.getServerId();
            RpcServerCache rpcServerCache = serverIdMap.get(serverId);
            if (rpcServerCache != null && rpcServerCache.checkClose(server)) {
                serverIdMap.remove(serverId);
                rpcServerCache.close();
                rpcServerCache = null;
            }
            if (rpcServerCache == null && StringUtils.isNotBlank(server.getHost()) && server.getRpcPort() > 0) {
                rpcServerCache = new RpcServerCache(server);
                serverIdMap.put(serverId, rpcServerCache);
            }
        }
        for (RpcServerCache value : serverIdMap.values()) {
            executor.submit(value::ensureAvailable);
        }
    }

    public RpcServerCache getRpcServerCache(int serverId) {
        RpcServerCache rpcServerCache = serverIdMap.get(serverId);
        return rpcServerCache == null || rpcServerCache.isAvailable() ? rpcServerCache : null;
    }

    public RpcServerCache randomRpcServerCache() {
        return RandomUtil.naturalRandomOne(serverIdMap.values().stream().filter(RpcServerCache::isAvailable).collect(Collectors.toList()));
    }
}
