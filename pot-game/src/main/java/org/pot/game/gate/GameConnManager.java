package org.pot.game.gate;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.alloc.MapAlloc;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.core.net.connection.ConnectionManagerListener;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FramePlayerMessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameConnManager implements ConnectionManagerListener<FramePlayerMessage> {
    private final Map<Integer, GameConn> map = MapAlloc.newSmallConcurrentHashMap();

    @Override
    public boolean onAdd(IConnection<FramePlayerMessage> connection) throws Exception {
        try {
            GameConn nextGameConn = new GameConn(connection);
            GameConn prevGameConn = map.put(connection.getId(), nextGameConn);
            if (prevGameConn != null) {
                log.error("GameConn Conflict Error. prev={}, conn={}", prevGameConn, nextGameConn);
                prevGameConn.close();
            }
            log.info("Add GameConn. conn={}", connection);
            return true;
        } catch (Throwable ex) {
            log.error("Add GameConn Error. conn={}", connection, ex);
            return false;
        }
    }

    @Override
    public void onRemove(IConnection<FramePlayerMessage> connection) throws Exception {
        log.info("Remove GameConn. conn={}", connection);
        GameConn con = map.remove(connection.getId());
        if (con != null) con.close();
    }

    public void close() {
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> {
            map.values().forEach(GameConn::close);
            return map.values().stream().allMatch(GameConn::isClosed);
        });
        map.values().removeIf(GameConn::isClosed);
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        for (GameConn conn : map.values()) {
            status.put(conn.getName(), conn.getPlayerSessionCount());
        }
        return status;
    }
}
