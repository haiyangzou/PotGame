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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

    public List<Server> selectAll() {
        List<Server> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return serverDao.selectAll();
    }
}
