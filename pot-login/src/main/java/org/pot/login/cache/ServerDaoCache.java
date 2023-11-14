package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.server.Server;
import org.pot.common.util.MapUtil;
import org.pot.login.dao.ServerDao;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ServerDaoCache {
    @Resource
    private ServerDao serverDao;
    private final AtomicReference<List<Server>> listReference = new AtomicReference<>(null);
    private final AtomicReference<Map<Integer, Map<Integer, Server>>> mpaReference = new AtomicReference<>(null);

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<Server> nextList = ImmutableList.copyOf(serverDao.selectAll());
        Map<Integer, Map<Integer, Server>> nextMap = new HashMap<>();
        nextList.forEach(server -> nextMap.computeIfAbsent(server.getTypeId(), k -> new TreeMap<>()).put(server.getServerId(), server));
        mpaReference.getAndUpdate(value -> MapUtil.immutableMapMap(nextMap));
    }

    public Collection<Server> selectType(int typeId) {
        Map<Integer, Map<Integer, Server>> map = mpaReference.get();
        if (map != null) {
            return map.getOrDefault(typeId, Collections.emptyMap()).values();
        }
        executeJob();
        return serverDao.selectType(typeId);
    }

    public List<Server> selectAll() {
        List<Server> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return serverDao.selectAll();
    }
}
