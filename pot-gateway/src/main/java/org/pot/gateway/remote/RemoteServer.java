package org.pot.gateway.remote;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.id.UniqueIdUtil;
import org.pot.common.util.RandomUtil;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FrameCmdMessage;
import org.pot.core.net.netty.FrameMessage;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.util.SignalLight;
import org.pot.gateway.engine.GatewayEngine;
import org.pot.message.protocol.DisConnectCode;
import org.pot.message.protocol.ServerNetPing;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
        if (this.shutdown) {
            try {
                shutdown();
            } catch (Exception ex) {
                log.error("RemoteServer shutdown occur an error. host={}, port={}", new Object[]{this.host, this.port, ex});
            } finally {
                this.runFuture.cancel(false);
                this.closed = true;
            }
        } else {
            try {
                execute();
            } catch (Exception ex) {
                log.error("RemoteServer run occur an error. host={}, port={}", new Object[]{this.host, this.port, ex});
            }
        }
    }

    private void execute() {
        long startTimeMillis = System.currentTimeMillis();
        SignalLight.setOn(this.KEEP);
        keepAlive();
        SignalLight.setOff(this.KEEP);
        SignalLight.setOn(this.TICK);
        for (int i = 0; i < this.connections.length(); i++) {
            IConnection<FramePlayerMessage> remoteServerConnection = this.connections.get(i);
            if (remoteServerConnection != null) {
                remoteServerConnection.flush();
                FramePlayerMessage framePlayerMessage;
                while ((framePlayerMessage = remoteServerConnection.pollRecvMessage()) != null) {
                    RemoteUser remoteUser = GatewayEngine.getInstance().getRemoteUserManager().getRemoteUser(framePlayerMessage.getPlayerId());
                    RemoteLogger.writeResponseLog(this, remoteUser, framePlayerMessage, startTimeMillis);
                    if (remoteUser == null)
                        continue;
                    IConnection<FrameCmdMessage> remoteUserConnection = remoteUser.getConnection();
                    if (remoteUserConnection == null)
                        continue;
                    if (framePlayerMessage.isProtoType(DisConnectCode.class)) {
                        remoteUserConnection.disconnect(new FrameCmdMessage(framePlayerMessage));
                        continue;
                    }
                    remoteUserConnection.sendMessage(new FrameCmdMessage(framePlayerMessage));
                }
            }
        }
        SignalLight.setOff(this.TICK);
    }

    private void keepAlive() {
        if (this.nextKeepAlive > System.currentTimeMillis())
            return;
        this.nextKeepAlive = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1L) + RandomUtil.randomLong(TimeUnit.SECONDS.toMillis(1L));
        boolean connect = false;
        for (int i = 0; i < this.connections.length(); i++) {
            IConnection<FramePlayerMessage> remoteServerConnection = this.connections.get(i);
            if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
                connect = true;
            } else {
                ServerNetPing ping = ServerNetPing.newBuilder().setTime(System.currentTimeMillis()).build();
                remoteServerConnection.sendMessage(new FramePlayerMessage(ping));
            }
        }
        if (connect && (this.connectFuture == null || this.connectFuture.isDone()))
            this.connectFuture = GatewayEngine.getInstance().getRemoteServerManager().connectRemoteServer(this);
    }

    IConnection<FramePlayerMessage> getConnection(RemoteUser remoteUser) {
        int index = UniqueIdUtil.index(remoteUser.getGameUid(), connections.length());
        return connections.get(index);
    }

    synchronized void setConnections() {
        NettyClientEngine<FramePlayerMessage> nettyClientEngine = GatewayEngine.getInstance().getNettyClientEngine();
        for (int i = 0; i < this.connections.length(); i++) {
            IConnection<FramePlayerMessage> prevConnection = this.connections.get(i);
            if (prevConnection == null || prevConnection.isClosed()) {
                IConnection<FramePlayerMessage> temp = nettyClientEngine.connect(this.host, this.port);
                if (temp == null)
                    return;
                temp.setImmediateFlush(false);
                prevConnection = this.connections.getAndUpdate(i, nextConnection -> temp);
                if (prevConnection != null)
                    prevConnection.close();
            }
        }
    }

    private void shutdown() {
        for (int i = 0; i < this.connections.length(); i++) {
            IConnection<FramePlayerMessage> remoteServerConnection = this.connections.get(i);
            if (remoteServerConnection != null)
                remoteServerConnection.close();
        }
    }
}
