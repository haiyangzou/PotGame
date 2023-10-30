package org.pot.gateway.remote;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FrameCmdMessage;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.util.SignalLight;
import org.pot.gateway.engine.GatewayEngine;
import org.pot.message.protocol.login.AppVersionInfoS2C;
import org.pot.message.protocol.login.LoginReConnectTokenKeyS2C;
import org.pot.message.protocol.login.LogoutReqC2S;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class RemoteUserGroup extends Thread {
    private final String WAIT;
    private final String TICK;
    private final String TICK_U;
    @Getter
    private final int index;

    private final BlockingQueue<RemoteUser> waitingUsers = new LinkedBlockingDeque<>();
    private final Map<Long, RemoteUser> remoteUsers = new ConcurrentHashMap<>();

    private volatile boolean shutdown = false;
    private volatile boolean closed = false;

    public RemoteUserGroup(int index) {
        this.index = index;
        String name = this.getClass().getSimpleName() + "-" + this.index;
        WAIT = name + "-Wait";
        TICK = name + "-Tick";
        TICK_U = name + "-TickUser@";
        this.setDaemon(false);
        this.setName(name);
        this.start();
    }

    boolean isClosed() {
        return closed;
    }

    void close() {
        shutdown = true;
    }

    @Override
    public void run() {
        long intervalMillis = GatewayEngine.getInstance().getConfig().getGatewayTickIntervalMillis();
        while (!shutdown) {
            try {
                ThreadUtil.run(false, intervalMillis, this::tick);
            } catch (Throwable ex) {
                log.error("RemoteUserGroup run occur an error.", ex);
            }
            try {
                shutdown();
            } catch (Throwable ex) {
                log.error("RemoteUserGroup shutdown occur an error.", ex);
            } finally {
                closed = true;
            }
        }
    }

    private void addWaitingUsers() {
        RemoteUser remoteUser;
        while ((remoteUser = waitingUsers.poll()) != null) {
            IConnection<FramePlayerMessage> remoteServerConnection = remoteUser.getRemoteServerConnection();
            if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
                remoteUser.disconnect(CommonErrorCode.SERVER_MAINTAIN);
                continue;
            }
            RemoteUser prevRemoteUser = remoteUsers.put(remoteUser.getGameUid(), remoteUser);
            if (prevRemoteUser != null) {
                prevRemoteUser.disconnect(CommonErrorCode.LOGIN_KICK);
                LogoutReqC2S logoutReqC2S = LogoutReqC2S.newBuilder().setGameUid(prevRemoteUser.getGameUid()).build();
                remoteServerConnection.sendMessage(new FramePlayerMessage(prevRemoteUser.getGameUid(), logoutReqC2S));
            }
            remoteServerConnection.sendMessage(new FramePlayerMessage(remoteUser.getGameUid(), remoteUser.getLoginDataS2S()));
            LoginReConnectTokenKeyS2C.Builder loginReconnectTokenKeyS2C = LoginReConnectTokenKeyS2C.newBuilder();
            loginReconnectTokenKeyS2C.setTokenKey(remoteUser.getLoginDataS2S().getToken());
            remoteUser.getConnection().sendMessage(new FrameCmdMessage(loginReconnectTokenKeyS2C.build()));
            AppVersionInfoS2C.Builder appVersionInfoS2C = AppVersionInfoS2C.newBuilder();
            appVersionInfoS2C.setAppUpdatePolicy(remoteUser.getLoginDataS2S().getAppUpdatePolicy());
            appVersionInfoS2C.setAppUpdateVersion(remoteUser.getLoginDataS2S().getAppUpdateVersion());
            appVersionInfoS2C.setAppUpdateUrl(StringUtils.stripToEmpty(remoteUser.getLoginDataS2S().getAppUpdateUrl()));
            remoteUser.getConnection().sendMessage(new FrameCmdMessage(appVersionInfoS2C.build()));
        }
    }

    private void tick() {
        SignalLight.watch(WAIT, this::addWaitingUsers);
        SignalLight.setOn(TICK);
        Iterator<RemoteUser> iterator = remoteUsers.values().iterator();
        while (iterator.hasNext()) {
            boolean remove = false;
            RemoteUser remoteUser = iterator.next();
            String flag = remoteUser.getSignalFlagTick();
            if (flag == null) {
                flag = TICK_U + remoteUser.getGameUid();
                remoteUser.setSignalFlagTick(flag);
            }
            SignalLight.setOn(flag);
            try {
                remove = remoteUser.tick();
            } catch (Exception exception) {
                remove = true;
                remoteUser.disconnect(CommonErrorCode.CONNECT_FAIL);
            } finally {
                if (remove) {
                    iterator.remove();
                }
            }
            SignalLight.setOff(flag);
        }
        SignalLight.setOff(TICK);
    }

    private void shutdown() {
        Iterator<RemoteUser> iterator = remoteUsers.values().iterator();
        while (iterator.hasNext()) {
            RemoteUser remoteUser = iterator.next();
            iterator.remove();
            remoteUser.disconnect(CommonErrorCode.SHUTDOWN_KICK);
        }
    }

    boolean addRemoteUser(RemoteUser remoteUser) {
        return waitingUsers.offer(remoteUser);
    }

    RemoteUser getRemoteUser(long gameUid) {
        return remoteUsers.get(gameUid);
    }

    int getRemoteUserCount() {
        return remoteUsers.size();
    }

    int getWaitingUserCount() {
        return waitingUsers.size();
    }
}
