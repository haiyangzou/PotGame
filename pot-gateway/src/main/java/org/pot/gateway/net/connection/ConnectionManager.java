package org.pot.gateway.net.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.channel.Channel;

import org.pot.gateway.net.netty.FrameMessage;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionManager<M extends FrameMessage> {
    private final long connectionIdleMills;
    private final Map<Channel, IConnection<M>> channelMap = new ConcurrentHashMap<>();

    public ConnectionManager(int connectionIdleSeconds) {
        this.connectionIdleMills = connectionIdleSeconds;
    }

    @Setter
    private volatile ConnectionManagerListener<M> listener;

    public IConnection<M> addConnection(Channel channel, String remoteHost, int remotePort) {
        if (channel == null) {
            log.error("Connection channel is null,remoteHost={},remotePort={}", remoteHost, remotePort);
        }
        IConnection<M> connection = channelMap.computeIfAbsent(channel,
                k -> new Connection<>(channel, remoteHost, remotePort));
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
