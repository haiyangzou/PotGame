package org.pot.game.engine.gate;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.pot.common.communication.server.Server;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.units.TimeUnitsConst;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.RandomUtil;
import org.pot.common.util.RunSignal;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.util.SignalLight;
import org.pot.game.PotGame;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.player.PlayerManager;
import org.pot.message.protocol.ServerNetPing;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.tunnel.*;
import scala.concurrent.impl.FutureConvertersImpl;

import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Tunnel extends Thread {
    private final String RECV;
    private final String SEND;
    private final String EXEC;
    @Getter
    private final Server server;
    @Getter
    private volatile boolean running = true;
    @Getter
    private volatile boolean closed = false;
    private volatile long nextKeepAlive = -1L;

    private volatile IConnection<FramePlayerMessage> connection;

    private final RunSignal saveSignal = new RunSignal(TimeUnitsConst.MILLIS_OF_5_MINUTES);
    private final Map<Long, TunnelPlayer> runningTunnelPlayerMap = new ConcurrentHashMap<>();
    private final Map<Long, TunnelPlayer> deletingTunnelPlayerMap = new ConcurrentHashMap<>();
    private final Queue<Pair<PlayerSession, LoginDataS2S>> reconnectingSessionQueue = new LinkedBlockingDeque<>();

    public Tunnel(Server server) {
        String name = this.getClass().getSimpleName() + "-" + server.getServerName();
        RECV = name + "-Recv";
        SEND = name + "-Send";
        EXEC = name + "-Exec";
        this.server = server;
        this.setDaemon(false);
        this.setName(name);
        this.start();
    }

    @Override
    public void run() {
        long intervalMillis = 30;
        while (running) {
            try {
                ThreadUtil.run(false, intervalMillis, this::tick);
            } catch (Throwable e) {
                log.error("{} run occur an error", this.getName(), e);
            }
        }
        this.shutdown();
        this.closed = true;
    }

    private void shutdown() {
        log.info("shutdown tunnel start,Server={},Address=({}:{})", server.getServerId(), server.getHost(), server.getPort());
        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        if (remoteServerConnection != null) {
            remoteServerConnection.close();
        }
        for (TunnelPlayer tunnelPlayer : runningTunnelPlayerMap.values()) {
            tunnelPlayer.disconnect(CommonErrorCode.SERVER_MAINTAIN);
        }
        this.save();
        TunnelManager.instance.removeTunnel(server);
    }

    private void tick() {
        keepAlive();
        saveSignal.run(this::save);
        SignalLight.watch(EXEC, this::exec);
        SignalLight.watch(RECV, this::recv);
        SignalLight.watch(SEND, this::send);
    }

    private void keepAlive() {
        if (nextKeepAlive > System.currentTimeMillis()) return;
        nextKeepAlive = System.currentTimeMillis()
                + TimeUnit.SECONDS.toMillis(1)
                + RandomUtil.randomLong(TimeUnit.SECONDS.toMillis(1));
        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
            connect();
        } else {
            ServerNetPing ping = ServerNetPing.newBuilder().setTime(System.currentTimeMillis()).build();
            remoteServerConnection.sendMessage(new FramePlayerMessage(ping));
        }
    }

    private void save() {

    }

    private void connect() {
        NettyClientEngine<FramePlayerMessage> nettyClientEngine = GameEngine.getInstance().getNettyClientEngine();
        IConnection<FramePlayerMessage> prevConnection = connection;
        if (prevConnection != null && !prevConnection.isClosed()) {
            return;
        }
        if (prevConnection != null) {
            prevConnection.close();
        }
        IConnection<FramePlayerMessage> temp = nettyClientEngine.connect(server.getHost(), server.getPort());
        if (temp != null) {
            temp.setImmediateFlush(false);
            connection = temp;
        }
    }

    private void recv() {
        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
            return;
        }
        FramePlayerMessage framePlayerMessage;
        while ((framePlayerMessage = remoteServerConnection.pollRecvMessage()) != null) {
            long uid = framePlayerMessage.getPlayerId();
            TunnelPlayer tunnelPlayer = runningTunnelPlayerMap.get(uid);
            if (uid < 0) {
                log.error("Invalid Tunnel Request,PlayerUid = {},Request={}", uid, framePlayerMessage.getProtoName());
                continue;
            }
            if (tunnelPlayer == null) {
                continue;
            }
            if (isGhostCmd(framePlayerMessage)) {
                processGhostCmd(tunnelPlayer, framePlayerMessage);
            } else {
                PlayerSession playerSession = tunnelPlayer.getPlayerSession();
                if (playerSession == null) {
                    continue;
                }
                playerSession.send(framePlayerMessage.getProto());
            }
        }
    }

    private void processGhostCmd(TunnelPlayer tunnelPlayer, FramePlayerMessage framePlayerMessage) {

    }

    private boolean isGhostCmd(FramePlayerMessage framePlayerMessage) {
        Class<? extends Message> protoType = framePlayerMessage.getProtoType();
        return protoType == GhostUpdateCmd.class || protoType == GhostFinishCmd.class;
    }

    private void send() {
        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        FramePlayerMessage framePlayerMessage;
        List<Long> idles = null;
        for (TunnelPlayer tunnelPlayer : runningTunnelPlayerMap.values()) {
            PlayerSession playerSession = tunnelPlayer.getPlayerSession();
            if (playerSession == null) {
                continue;
            }
            while ((framePlayerMessage = playerSession.pollRecvMessage()) != null) {
                remoteServerConnection.sendMessage(framePlayerMessage);
            }
            if (playerSession.isIdle()) {
                if (idles == null) idles = new ArrayList<>();
                idles.add(playerSession.getUid());
            }
        }
        if (CollectionUtil.isNotEmpty(idles)) {
            idles.forEach(uid -> {
                TunnelPlayer tunnelPlayer = runningTunnelPlayerMap.get(uid);
                if (tunnelPlayer != null) {
                    tunnelPlayer.setPlayerSession(null);
                }
            });
        }
        remoteServerConnection.flush();
    }

    private void exec() {
        if (!deletingTunnelPlayerMap.isEmpty()) {
            Iterator<TunnelPlayer> iterator = deletingTunnelPlayerMap.values().iterator();
            while (iterator.hasNext()) {
                TunnelPlayer tunnelPlayer = iterator.next();
                IConnection<FramePlayerMessage> remoteServerConnection = connection;
                if (remoteServerConnection != null && !remoteServerConnection.isClosed()) {
                    GhostDestroyCmd.Builder builder = GhostDestroyCmd.newBuilder().setPlayerId(tunnelPlayer.getPlayerUid());
                    remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
                    iterator.remove();
                }
            }
        }
        {
            Pair<PlayerSession, LoginDataS2S> pair;
            while ((pair = reconnectingSessionQueue.poll()) != null) {
                PlayerSession playerSession = pair.getLeft();
                LoginDataS2S loginDataS2S = pair.getRight();
                boolean exists = false;
                TunnelPlayer tunnelPlayer = runningTunnelPlayerMap.get(loginDataS2S.getGameUid());
                if (tunnelPlayer != null) {
                    PlayerSession oldPlayerSession = tunnelPlayer.setPlayerSession(playerSession);
                    if (oldPlayerSession != null && oldPlayerSession != playerSession) {
                        oldPlayerSession.disconnect(CommonErrorCode.LOGIN_KICK);
                    }
                    exists = true;
                }
                if (exists) {
                    IConnection<FramePlayerMessage> remoteServerConnection = connection;
                    if (remoteServerConnection == null || remoteServerConnection.isConnected()) {
                        playerSession.disconnect(CommonErrorCode.SERVER_MAINTAIN);
                    } else {
                        GhostReconnectCmd.Builder builder = GhostReconnectCmd.newBuilder();
                        builder.setPlayerId(loginDataS2S.getGameUid());
                        builder.setLoginDataS2S(loginDataS2S);
                        playerSession.establish(builder.build());
                    }
                } else {
                    LoginDataS2S finalLoginDataS2S = PlayerManager.getInstance().loginPlayer(playerSession, loginDataS2S);
                    if (finalLoginDataS2S == null) {
                        playerSession.disconnect(CommonErrorCode.LOGIN_FAIL);
                    } else {
                        playerSession.establish(finalLoginDataS2S);
                    }
                }
            }
        }
        {
            final long currentTimeMills = System.currentTimeMillis();
            for (TunnelPlayer tunnelPlayer : runningTunnelPlayerMap.values()) {
                if (tunnelPlayer.getState() == TunnelPlayerState.PREPARE) {
                    log.debug("Tunnel Player Preparing,PlayerUid={}", tunnelPlayer.getPlayerUid());
                } else if (tunnelPlayer.getState() == TunnelPlayerState.READY) {
                    IConnection<FramePlayerMessage> remoteServerConnection = connection;
                    try {
                        GhostEnterCmd.Builder builder = GhostEnterCmd.newBuilder();
                        builder.setPlayerId(tunnelPlayer.getPlayerUid());
                        builder.setPlayerData(TunnelUtil.savePlayerData(tunnelPlayer.getPlayerData()));
                        builder.setVisaData(TunnelUtil.saveVisaData(tunnelPlayer.getVisaData()));
                        remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
                        tunnelPlayer.setState(TunnelPlayerState.WAIT);
                    } catch (Throwable throwable) {
                        tunnelPlayer.disconnect(CommonErrorCode.CONNECT_FAIL);
                        recover(tunnelPlayer);
                    }
                } else if (tunnelPlayer.getState() == TunnelPlayerState.WAIT) {
                    IConnection<FramePlayerMessage> remoteServerConnection = connection;
                    if (remoteServerConnection == null || remoteServerConnection.isConnected()) {
                        tunnelPlayer.disconnect(CommonErrorCode.SERVER_MAINTAIN);
                        recover(tunnelPlayer);
                    } else {
                        log.debug("Tunnel player waiting,PlayerUid={}", tunnelPlayer.getPlayerUid());
                    }
                } else if (tunnelPlayer.getState() == TunnelPlayerState.RUN) {
                    IConnection<FramePlayerMessage> remoteServerConnection = connection;
                    if (remoteServerConnection == null || remoteServerConnection.isConnected()) {
                        tunnelPlayer.disconnect(CommonErrorCode.SERVER_MAINTAIN);
                        continue;
                    }
                    if (tunnelPlayer.keepAlive(currentTimeMills)) {
                        GhostKeepAliveCmd.Builder builder = GhostKeepAliveCmd.newBuilder().setPlayerId(tunnelPlayer.getPlayerUid());
                        remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
                    }
                    long timeout = tunnelPlayer.getVisaData().getTimeout();
                    if (timeout <= 0 || timeout < currentTimeMills) {
                        continue;
                    }
                    GhostExitCmd.Builder builder = GhostExitCmd.newBuilder().setPlayerId(tunnelPlayer.getPlayerUid());
                    remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
                }
            }
        }
    }

    private void recover(TunnelPlayer tunnelPlayer) {

    }
}