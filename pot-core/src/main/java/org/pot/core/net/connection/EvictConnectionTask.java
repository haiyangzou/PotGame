package org.pot.core.net.connection;

import java.util.List;

import org.pot.core.net.netty.FrameMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EvictConnectionTask<M extends FrameMessage> implements Runnable {
    private final ConnectionManager<M> connectionManager;

    public EvictConnectionTask(ConnectionManager<M> connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            int close = 0, remove = 0;
            List<IConnection<M>> idleConnections = connectionManager.getIdleConnections();

            for (IConnection<M> connection : idleConnections) {
                if (connection.isClosed()) {
                    remove++;
                    connectionManager.removeConnection(connection);
                } else {
                    close++;
                    connection.close();
                }
            }
            long millis = System.currentTimeMillis() - startTime;
            if (close > 0 || remove > 0 || millis > 0) {
                log.info("EvictConnectionTask close idle connection count:{}, remove{},cost:{}", close, remove, millis);
            }

        } catch (Throwable e) {
            log.error("evict connection task error", e);
        }
    }

}
