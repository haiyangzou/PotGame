package org.pot.gateway.remote;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.id.UniqueIdUtil;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.gateway.engine.GatewayEngine;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Slf4j
public class RemoteServer implements Runnable {
    private final String KEEP;
    private final String TICK;
    @Getter
    private final Integer serverId;
    @Getter
    @Setter
    private volatile String host;
    @Getter
    @Setter
    private volatile int port;
    private volatile Future<?> connectFuture;
    private volatile ScheduledFuture<?> runFuture;
    private volatile boolean shutdown = false;
    private volatile boolean closed = false;
    private volatile long nextKeepAlive = -1L;
    private final AtomicReferenceArray<IConnection<FramePlayerMessage>> connections
            = new AtomicReferenceArray<>(GatewayEngine.getInstance().getConfig().getServerConnectionSize());

    public RemoteServer(Integer serverId, String host, int port) {
        String name = this.getClass().getSimpleName() + "-" + serverId;
        KEEP = name + "-Keep";
        TICK = name + "-Tick";
        this.serverId = serverId;
        this.host = host;
        this.port = port;
        this.runFuture = GatewayEngine.getInstance().getRemoteServerManager().runRemoteServer(this);
    }

    boolean isClosed() {
        return closed || runFuture.isDone();
    }

    void close() {
        shutdown = true;
    }

    @Override
    public void run() {

    }

    IConnection<FramePlayerMessage> getConnection(RemoteUser remoteUser) {
        int index = UniqueIdUtil.index(remoteUser.getGameUid(), connections.length());
        return connections.get(index);
    }
}
