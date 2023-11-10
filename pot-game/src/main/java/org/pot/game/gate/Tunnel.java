package org.pot.game.gate;

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
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.mapper.PlayerTunnelEntityMapper;
import org.pot.message.protocol.ServerNetPing;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.tunnel.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    void close() {
        running = false;
        log.info("Close Tunnel,ServerType= {},ServerId={},ServerHost={}", server.getTypeName(), server.getServerId(), server.getHost());
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

    int getPlayerSize() {
        return runningTunnelPlayerMap.size();
    }

    int getOnlinePlayerSize() {
        int size = 0;
        for (TunnelPlayer tunnelPlayer : runningTunnelPlayerMap.values()) {
            if (tunnelPlayer.isOnline()) size++;
        }
        return size;
    }

    void quit() {
        for (TunnelPlayer tunnelPlayer : runningTunnelPlayerMap.values()) {
            tunnelPlayer.getVisaData().setTimeout(System.currentTimeMillis());
            log.info("Quit Tunnel Player,PlayerUid={},State={},ServerType={},ServerId={},ServerHost={}", tunnelPlayer.getPlayerUid(), tunnelPlayer.getState(), server.getTypeName(), server.getServerId(), server.getHost());
        }
    }

    void quit(long playerUid) {
        TunnelPlayer tunnelPlayer = runningTunnelPlayerMap.get(playerUid);
        if (tunnelPlayer != null) {
            tunnelPlayer.getVisaData().setTimeout(System.currentTimeMillis());
        }
    }

    boolean isTunnelling(long playerUid) {
        TunnelPlayer tunnelPlayer = runningTunnelPlayerMap.get(playerUid);
        if (tunnelPlayer == null) return false;
        return tunnelPlayer.getState() != TunnelPlayerState.PREPARE;
    }

    synchronized boolean reconnect(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        if (contains(loginDataS2S.getGameUid())) {
            reconnectingSessionQueue.offer(Pair.of(playerSession, loginDataS2S));
            return true;
        }
        return false;
    }

    synchronized void add(TunnelPlayer tunnelPlayer) {
        runningTunnelPlayerMap.putIfAbsent(tunnelPlayer.getPlayerUid(), tunnelPlayer);
        log.info("Add Tunnel Player,PlayerUid={}", tunnelPlayer.getPlayerUid());
    }

    synchronized void del(TunnelPlayer tunnelPlayer) {
        recover(tunnelPlayer);
        deletingTunnelPlayerMap.putIfAbsent(tunnelPlayer.getPlayerUid(), tunnelPlayer);
        log.info("Del Tunnel Player,PlayerUid={}", tunnelPlayer.getPlayerUid());
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
        SqlSession sqlSession = GameDb.local().getSqlSession(PlayerTunnelEntityMapper.class);
        sqlSession.submitWithoutResult(PlayerTunnelEntityMapper.class,
                m -> m.batchInsertOnDuplicateKeyUpdate(runningTunnelPlayerMap.values().stream().map(TunnelPlayer::toEntity).collect(Collectors.toList())),
                () -> log.info("save tunnel player success."),
                () -> log.error("save tunnel player failed.")
        );
        sqlSession.submitWithoutResult(PlayerTunnelEntityMapper.class,
                m -> m.deleteNotInKeyList(runningTunnelPlayerMap.values().stream().map(TunnelPlayer::toEntity).collect(Collectors.toList())),
                () -> log.info("delete tunnel player success."),
                () -> log.error("delete tunnel player failed.")
        );
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
                log.error("Not Exists Tunnel Player. PlayerUid={},Request={}", uid, framePlayerMessage.getProtoName());
                if (framePlayerMessage.isProtoType(GhostUpdateCmd.class) || framePlayerMessage.isProtoType(GhostFinishCmd.class)) {
                    GhostDestroyCmd.Builder builder = GhostDestroyCmd.newBuilder().setPlayerId(framePlayerMessage.getPlayerId());
                    remoteServerConnection.sendMessage(new FramePlayerMessage(framePlayerMessage.getPlayerId(), builder.build()));
                }
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
        if (framePlayerMessage.isProtoType(GhostUpdateCmd.class)) {
            update(tunnelPlayer, framePlayerMessage.getProto());
        } else if (framePlayerMessage.isProtoType(GhostFinishCmd.class)) {
            finish(tunnelPlayer, framePlayerMessage.getProto());
        } else if (framePlayerMessage.isProtoType(GhostEnterErrorCmd.class)) {
            tunnelPlayer.disconnect(CommonErrorCode.CONNECT_FAIL);
            recover(tunnelPlayer);
        } else if (framePlayerMessage.isProtoType(GhostEnterSuccessCmd.class)) {
            tunnelPlayer.setState(TunnelPlayerState.RUN);
            SqlSession sqlSession = GameDb.local().getSqlSession(PlayerTunnelEntityMapper.class);
            sqlSession.submitWithoutResult(PlayerTunnelEntityMapper.class,
                    m -> m.insertOnDuplicateKeyUpdate(tunnelPlayer.toEntity()),
                    () -> log.info("save tunnel player success."),
                    () -> log.error("save tunnel player failed.")
            );
        } else if (framePlayerMessage.isProtoType(GhostExitErrorCmd.class)) {
            tunnelPlayer.disconnect(CommonErrorCode.CONNECT_FAIL);
            recover(tunnelPlayer);
        } else if (framePlayerMessage.isProtoType(GhostReconnectErrorCmd.class)) {
            tunnelPlayer.disconnect(CommonErrorCode.CONNECT_FAIL);
            recover(tunnelPlayer);
        }

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
        {
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
                    if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
                        tunnelPlayer.disconnect(CommonErrorCode.SERVER_MAINTAIN);
                        recover(tunnelPlayer);
                        continue;
                    }
                    SqlSession sqlSession = GameDb.local().getSqlSession(PlayerTunnelEntityMapper.class);
                    sqlSession.submitWithoutResult(PlayerTunnelEntityMapper.class,
                            m -> m.batchInsertOnDuplicateKeyUpdate(runningTunnelPlayerMap.values().stream().map(TunnelPlayer::toEntity).collect(Collectors.toList())),
                            () -> log.info("Tunnel Player Ready save success."),
                            () -> log.error("Tunnel Player Ready save failed.")
                    );
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
                    if (tunnelPlayer.getRunSignal().signal()) {
                        long timeout = tunnelPlayer.getVisaData().getTimeout();
                        if (timeout <= 0 || timeout > currentTimeMills) {
                            //不显示或为到时间，发送保持活动消息给远端服务器
                            GhostKeepAliveCmd.Builder builder = GhostKeepAliveCmd.newBuilder().setPlayerId(tunnelPlayer.getPlayerUid());
                            remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
                        }
                    } else {
                        //发送返回消息给远端服务器，然后等待返回消息，重建玩家
                        GhostExitCmd.Builder builder = GhostExitCmd.newBuilder().setPlayerId(tunnelPlayer.getPlayerUid());
                        remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
                    }
                }
            }
        }
    }

    private void recover(TunnelPlayer tunnelPlayer) {
        tunnelPlayer.recover();
        runningTunnelPlayerMap.remove(tunnelPlayer.getPlayerUid());
        //save db
        SqlSession sqlSession = GameDb.local().getSqlSession(PlayerTunnelEntityMapper.class);
        sqlSession.submitWithoutResult(PlayerTunnelEntityMapper.class,
                m -> m.delete(tunnelPlayer.getPlayerUid()),
                () -> log.info("Tunnel Player Ready save success."),
                () -> log.error("Tunnel Player Ready save failed."));
    }

    private void update(TunnelPlayer tunnelPlayer, GhostUpdateCmd ghostUpdateCmd) {
        try {
            tunnelPlayer.setPlayerData(TunnelUtil.loadPlayerData(ghostUpdateCmd.getPlayerData()));
        } catch (Throwable throwable) {
            log.error("Tunnel Player Update Error,PlayerUid={}", tunnelPlayer.getPlayerUid(), throwable);
        }
    }

    public synchronized boolean join(Player player, TunnelVisaData tunnelVisaData) {
        if (!isRunning() || isClosed()) return false;
        if (contains(player.getUid())) return false;

        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        if (remoteServerConnection == null && remoteServerConnection.isConnected()) return false;
        TunnelPlayer tunnelPlayer = new TunnelPlayer(player.getUid(), tunnelVisaData);
        tunnelPlayer.setState(TunnelPlayerState.PREPARE);
        tunnelPlayer.getVisaData().setTargetServerId(server.getServerIdObject());
        runningTunnelPlayerMap.putIfAbsent(tunnelPlayer.getPlayerUid(), tunnelPlayer);
        //save db
        tunnelPlayer.join();
        return false;
    }

    private void finish(TunnelPlayer tunnelPlayer, GhostFinishCmd ghostFinishCmd) {
        try {
            tunnelPlayer.setVisaData(TunnelUtil.loadVisaData(ghostFinishCmd.getVisaData()));
            tunnelPlayer.setPlayerData(TunnelUtil.loadPlayerData(ghostFinishCmd.getPlayerData()));
        } catch (Throwable throwable) {
            log.error("Tunnel player Finish Error,PlayerUid={}", tunnelPlayer.getPlayerUid(), throwable);
        }
        if (TunnelManager.instance.redirect(tunnelPlayer)) {
            runningTunnelPlayerMap.remove(tunnelPlayer.getPlayerUid());
        } else {
            recover(tunnelPlayer);
        }
        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        if (remoteServerConnection != null && !remoteServerConnection.isConnected()) {
            GhostDestroyCmd.Builder builder = GhostDestroyCmd.newBuilder().setPlayerId(tunnelPlayer.getPlayerUid());
            remoteServerConnection.sendMessage(new FramePlayerMessage(tunnelPlayer.getPlayerUid(), builder.build()));
        }
    }

    boolean contains(long playerUid) {
        return runningTunnelPlayerMap.containsKey(playerUid);
    }

    synchronized boolean redirect(TunnelPlayer tunnelPlayer) {
        if (!isRunning() || isClosed()) return false;
        if (contains(tunnelPlayer.getPlayerUid())) return false;

        IConnection<FramePlayerMessage> remoteServerConnection = connection;
        if (remoteServerConnection == null && remoteServerConnection.isConnected()) return false;
        tunnelPlayer.setState(TunnelPlayerState.READY);
        tunnelPlayer.getVisaData().setTargetServerId(server.getServerIdObject());
        runningTunnelPlayerMap.putIfAbsent(tunnelPlayer.getPlayerUid(), tunnelPlayer);
        SqlSession sqlSession = GameDb.local().getSqlSession(PlayerTunnelEntityMapper.class);
        sqlSession.submitWithoutResult(
                PlayerTunnelEntityMapper.class,
                m -> m.insertOnDuplicateKeyUpdate(tunnelPlayer.toEntity()),
                () -> log.info("redirect tunnel player success"),
                () -> log.error("")
        );
        return true;
    }
}
