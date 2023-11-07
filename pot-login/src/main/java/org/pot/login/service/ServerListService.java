package org.pot.login.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerType;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.id.UniqueIdUtil;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.dal.redis.lock.RedisLock;
import org.pot.dal.redis.lock.RedisLockFactory;
import org.pot.login.cache.GameServerDaoCache;
import org.pot.login.cache.ServerDaoCache;
import org.pot.login.dao.GameServerDao;
import org.pot.login.dao.ServerDao;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerListService {
    @Resource
    private ServerDao serverDao;
    @Resource
    private GameServerDao gameServerDao;
    @Resource
    private ServerDaoCache serverDaoCache;
    @Resource
    private GameServerDaoCache gameServerDaoCache;
    @Resource
    private ReactiveStringRedisTemplate globalReactiveRedisTemplate;

    private RedisLock mutex;

    @PostConstruct
    private void initRedisLock() {
        mutex = RedisLockFactory.getRedisLock(this.getClass().getSimpleName(), globalReactiveRedisTemplate);
    }

    public Server findServerOnNotExistsAutoCreate(int typeId, String host, int port, int httpPort, int rpcPort) throws Exception {
        if (typeId < 0 || StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("Illegal Server Host");
        }
        if (port <= 0 && httpPort <= 0 && rpcPort <= 0) {
            throw new IllegalArgumentException("Illegal Server port");
        }
        List<Server> servers = ImmutableList.copyOf(serverDaoCache.selectAll());
        Server server = findServer(servers, typeId, host, port, httpPort, rpcPort);
        if (server != null) {
            return server;
        }
        try {
            mutex.lock();
            servers = ImmutableList.copyOf(serverDao.selectAll());
            server = findServer(servers, typeId, host, port, httpPort, rpcPort);
            if (server != null) {
                return server;
            }
            if (typeId == ServerType.GAME_SERVER.getId()) {
                return null;
            }
            if (isIllegalServerHostAndPort(servers, host, port, httpPort, rpcPort)) {
                throw new IllegalArgumentException("Illegal Server Address");
            }
            if (ServerType.SLAVE_SERVER.getId() == typeId) {
                int newId = servers.stream().filter(s -> typeId == s.getTypeId()).mapToInt(Server::getServerId).min().orElse(UniqueIdUtil.MAX_SERVER_ID) - 1;
                Preconditions.checkArgument(newId >= 1, "Server Id is Exhausted,Type:" + typeId);
                server = new Server(typeId, newId, host, port, httpPort, rpcPort);
                LocalDateTime date = LocalDateTime.of(2022, 10, 10, 0, 0, 0);
                server.setOpenTime(DateTimeUtil.toUtilDate(date));
            } else {
                int newId = servers.stream().filter(s -> typeId == s.getTypeId()).mapToInt(Server::getServerId).max().orElse(0) + 1;
                Preconditions.checkArgument(newId <= UniqueIdUtil.MAX_SERVER_ID, "Server Id is Exhausted,Type:" + typeId);
                server = new Server(typeId, newId, host, port, httpPort, rpcPort);
            }
            serverDao.insert(server);
            return server;
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            mutex.unlock();
        }

    }

    private boolean isIllegalServerHostAndPort(List<Server> servers, String host, int port, int httpPort, int rpcPort) {
        List<Integer> ports = Lists.newArrayList(port, httpPort, rpcPort);
        ports.removeIf(checkPort -> checkPort <= 0);
        if (ports.isEmpty()) return true;
        List<String> hosts = Ipv4Util.split(host);
        if (hosts.isEmpty()) return true;
        for (Server server : servers) {
            List<String> serverHosts = Ipv4Util.split(server.getHost());
            if (CollectionUtils.containsAny(serverHosts, hosts)) {
                List<Integer> serverPorts = Lists.newArrayList(server.getPort(), server.getHttpPort(), server.getRpcPort());
                serverPorts.removeIf(serverPort -> serverPort <= 0);
                if (CollectionUtils.containsAny(serverPorts, ports)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Server findServer(List<Server> servers, int typeId, String host, int port, int httpPort, int rpcPort) {
        List<Server> results = servers.stream().filter(s -> typeId == s.getTypeId())
                .filter(s -> port == s.getPort() && httpPort == s.getHttpPort() && rpcPort == s.getRpcPort())
                .filter(s -> CollectionUtils.containsAll(Ipv4Util.split(host), Ipv4Util.split(s.getHost())))
                .collect(Collectors.toList());
        if (results.size() > 1) {
            log.error("Repeated Server Error");
            return null;
        }
        if (results.size() == 1) {
            return results.get(0);
        }
        return null;
    }
}
