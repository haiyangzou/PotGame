package org.pot.core.net.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import org.pot.common.alloc.MapAlloc;
import org.pot.core.net.netty.FrameMessage;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionManager<M extends FrameMessage> {
    private final long connectionIdleMills;
    private final Map<Channel, IConnection<M>> channelMap = MapAlloc.newMediumConcurrentHashMap();

    public ConnectionManager(int connectionIdleSeconds) {
        this.connectionIdleMills = TimeUnit.SECONDS.toMillis(connectionIdleSeconds);
    }

    @Setter
    private volatile ConnectionManagerListener<M> listener;

    public IConnection<M> addConnection(SocketChannel channel) {
        return addConnection(channel, channel.remoteAddress().getAddress().getHostAddress(),
                channel.remoteAddress().getPort());
    }

    public IConnection<M> addConnection(Channel channel, String remoteHost, int remotePort) {
        if (channel == null) {
            log.error("Connection channel is null,remoteHost={},remotePort={}", remoteHost, remotePort);
            return null;
        }
        IConnection<M> connection = channelMap.computeIfAbsent(channel,
                k -> new Connection<>(channel, remoteHost, remotePort));
        try {
            ConnectionManagerListener<M> tempListener = this.listener;
            if (tempListener != null && !tempListener.onAdd(connection)) {
                log.error("connection manager listener onAdd failed conn{}", connection);
                connection.close();
                return null;
            }
        } catch (Exception e) {
            log.error("connection manager listener onAdd error conn{}", connection, e);
            connection.close();
            return null;
        }
        return connection;
    }

    public List<IConnection<M>> getIdleConnections() {
        List<IConnection<M>> connections = new ArrayList<>();
        for (IConnection<M> connection : channelMap.values()) {
            if (connection.isIdle(connectionIdleMills)) {
                connections.add(connection);
            }
        }
        return connections;
    }

    public IConnection<M> getConnection(Channel channel) {
        return channel == null ? null : channelMap.get(channel);
    }

    public IConnection<M> removeConnection(Channel channel) {
        IConnection<M> connection = getConnection(channel);
        removeConnection(connection);
        return connection;
    }

    public void removeConnection(IConnection<M> connection) {
        if (connection != null && connection.getChannel() != null) {
            if (this.channelMap.remove(connection.getChannel()) != null) {
                try {
                    ConnectionManagerListener<M> tempListener = listener;
                    if (tempListener != null) {
                        tempListener.onRemove(connection);
                    }
                } catch (Exception e) {
                    log.error("connection manager listner onRemove error,conn={}", connection, e);
                }
            }
        }
    }

}
