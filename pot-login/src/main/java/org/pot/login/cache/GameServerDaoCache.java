package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import org.pot.common.communication.server.GameServer;
import org.pot.common.util.MapUtil;
import org.pot.login.dao.GameServerDao;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class GameServerDaoCache {
    private final AtomicReference<Map<Integer, GameServer>> mapReference = new AtomicReference<>();
    private final AtomicReference<List<GameServer>> listReference = new AtomicReference<>();
    private final AtomicReference<GameServer> newGameServerReference = new AtomicReference<>();

    @Resource
    private GameServerDao gameServerDao;

    @PostConstruct
    private void init() {
        executeJob();
    }

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<GameServer> nextList = ImmutableList.copyOf(gameServerDao.selectAll());
        List<GameServer> prevList = listReference.getAndUpdate(value -> nextList);
        mapReference.getAndUpdate(value -> MapUtil.immutableMap(nextList, GameServer::getServerId));
    }

    private void setNewServerInfo(List<GameServer> gameServers) {
        long now = System.currentTimeMillis();
        long future = now + 8 * 60 * 60 * 1000;
        GameServer newGameServer = null;
        for (GameServer gameServer : gameServers) {
            //当前时间后，8小时内的都计算为推荐服
            if (gameServer.getOpenTimeStamp() > now && gameServer.getOpenTimeStamp() < future) {
                if (newGameServer == null) {
                    newGameServer = gameServer;
                } else if (newGameServer.getOpenTimeStamp() > gameServer.getOpenTimeStamp()) {
                    newGameServer = gameServer;
                }
            }
        }
        newGameServerReference.set(newGameServer);
    }

    public List<GameServer> selectAll() {
        List<GameServer> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return gameServerDao.selectAll();
    }

    public GameServer selectOne(int serverId) {
        Map<Integer, GameServer> map = mapReference.get();
        if (map != null) return map.get(serverId);
        executeJob();
        return gameServerDao.selectOne(serverId);
    }

    public GameServer getNewGameServer() {
        return newGameServerReference.get();
    }
}
