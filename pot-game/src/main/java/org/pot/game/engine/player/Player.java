package org.pot.game.engine.player;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.player.PlayerCaches;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.common.util.ExDateTimeUtil;
import org.pot.common.util.RunSignal;
import org.pot.common.util.StringUtil;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.util.NewDay;
import org.pot.core.util.SignalLight;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.StatisticsEnum;
import org.pot.game.engine.player.async.AbstractAsyncHandler;
import org.pot.game.engine.player.async.AsyncHandlerManager;
import org.pot.game.engine.player.common.PlayerCommonAgent;
import org.pot.game.engine.player.component.*;
import org.pot.game.engine.player.module.PlayerAgentsInitializer;
import org.pot.game.engine.player.module.army.PlayerArmyAgent;
import org.pot.game.engine.player.module.chat.PlayerChatAgent;
import org.pot.game.engine.player.module.event.PlayerEventsInitializer;
import org.pot.game.engine.player.module.event.event.PlayerFirstLogin;
import org.pot.game.engine.player.module.event.event.PlayerLogin;
import org.pot.game.engine.player.module.ghost.PlayerGhostAgent;
import org.pot.game.engine.player.module.hero.PlayerHeroAgent;
import org.pot.game.engine.player.module.resource.PlayerResourceAgent;
import org.pot.game.engine.player.module.tower.PlayerTowerAgent;
import org.pot.game.engine.player.union.PlayerUnionAgent;
import org.pot.game.gate.PlayerSession;
import org.pot.game.persistence.entity.PlayerProfileEntity;
import org.pot.game.util.PlayerSnapshotUtil;
import org.pot.message.protocol.ProtocolSupport;
import org.pot.message.protocol.login.*;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Player {
    @Getter
    private final long uid;
    private final AsyncRunner asyncRunner;
    private volatile PlayerSession playerSession;
    private volatile PlayerData playerData;
    private volatile LoginDataS2S loginDataS2S;
    public final PlayerEventComponent eventComponent = new PlayerEventComponent(this);
    public final PlayerSceneComponent sceneComponent = new PlayerSceneComponent(this);
    public final PlayerCommonAgent commonAgent = new PlayerCommonAgent(this);
    public final PlayerPushComponent pushComponent = new PlayerPushComponent(this);
    public final PlayerPowerComponent powerComponent = new PlayerPowerComponent(this);
    public final PlayerStatisticsComponent statisticsComponent = new PlayerStatisticsComponent(this);
    public final PlayerUnionAgent unionAgent = new PlayerUnionAgent(this);

    public final PlayerTowerAgent towerAgent = new PlayerTowerAgent(this);

    public final PlayerArmyAgent armyAgent = new PlayerArmyAgent(this);
    public final PlayerChatAgent chatAgent = new PlayerChatAgent(this);
    public final PlayerHeroAgent heroAgent = new PlayerHeroAgent(this);
    public final PlayerResourceAgent resourceAgent = new PlayerResourceAgent(this);

    @Getter
    public final List<PlayerAgentAdapter> agentAdapterList;
    @Getter
    private volatile PlayerProfileEntity profile;
    public final PlayerGhostAgent ghostAgent = new PlayerGhostAgent(this);
    @Getter
    private final AtomicReference<PlayerState> state = new AtomicReference<>(PlayerState.initial);
    /**
     * 卸载检测的时候跳过加载时间超时检测一次
     * 用于临时加载一次player之后需要尽快卸载使用
     * 检测之后立即设置为false
     */
    private boolean skipLoadTimeCheckOnce = false;

    public int getServerId() {
        return profile.getServerId();
    }

    public Player(long uid) {
        this.uid = uid;
        this.playerSession = null;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
        PlayerEventsInitializer.INITIALIZER.initEvents(this);
        agentAdapterList = ImmutableList.copyOf(PlayerAgentsInitializer.INITIALIZER.initAgents(this));
        agentAdapterList.forEach(PlayerAgentAdapter::initSignalFlags);
        this.playerData = new PlayerData(this.uid);
        this.playerData.asyncLoad(
                data -> log.info("Uid Load Player Success,uid={}", data.getUid()),
                data -> log.info("UId Load Player Fail !uid={}", data.getUid())
        );
    }

    public String getName() {
        return profile.getName();
    }

    public Player(PlayerSession playerSession, PlayerData playerData) {
        this.uid = playerData.getUid();
        this.playerSession = playerSession;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
        PlayerEventsInitializer.INITIALIZER.initEvents(this);
        agentAdapterList = ImmutableList.copyOf(PlayerAgentsInitializer.INITIALIZER.initAgents(this));
        agentAdapterList.forEach(PlayerAgentAdapter::initSignalFlags);
        this.playerData = playerData;
        this.playerData.asyncLoad(
                data -> log.info("Uid Load Player Success,uid={}", data.getUid()),
                data -> log.info("UId Load Player Fail !uid={}", data.getUid())
        );
        this.state.updateAndGet(s -> PlayerState.loading);
    }

    public IErrorCode onLoginSuccess(LoginSuccessC2S loginSuccessC2S) {
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            String flag = StringUtil.format("Player@{}_onLoginSuccess_{}", this.uid, playerAgent.getClassSimpleName());
            SignalLight.setOn(flag);
            try {
                playerAgent.onLoginSuccess();
            } catch (Exception e) {
                log.error("Player On Login Success Error! uid={}, Agent={}", getUid(), playerAgent.getClassSimpleName(), e);
            }
            SignalLight.setOff(flag);
        }
        if (!ExDateTimeUtil.isSameDay(this.profile.getLastLoginTime(), System.currentTimeMillis())) {
            this.statisticsComponent.addOneStatisticsInt(StatisticsEnum.LOGIN_COUNT);
            this.eventComponent.postPlayerEvent(PlayerFirstLogin.builder().build());
        }
        this.eventComponent.postPlayerEvent(PlayerLogin.builder().build());
        this.profile.setLastLoginTime(System.currentTimeMillis());
        this.profile.setLastOnlineTime(System.currentTimeMillis());
        PlayerCaches.snapShot().login(getUid());
        return null;
    }

    public IErrorCode onLogin(LoginDataS2S loginDataS2S) {
        this.profile.setLoginIp(loginDataS2S.getIp());
        this.profile.setLoginDeviceOs(loginDataS2S.getLoginReqC2S().getDeviceOS());
        this.profile.setAppVersion(loginDataS2S.getLoginReqC2S().getAppVersion());
        this.profile.setOsVersion(loginDataS2S.getLoginReqC2S().getOsVersion());
        this.profile.setDevice(loginDataS2S.getLoginReqC2S().getDevice());
        this.profile.setChannel(loginDataS2S.getLoginReqC2S().getChannel());
        this.profile.setPlatform(loginDataS2S.getLoginReqC2S().getPlatform());
        this.profile.setCountry(loginDataS2S.getCountry());
        LoginRespS2C.Builder builder = LoginRespS2C.newBuilder();
        builder.setAccount(loginDataS2S.getLoginReqC2S().getAccount());
        builder.setAccountUid(loginDataS2S.getAccountUid());
        builder.setServerId(loginDataS2S.getServerId());
        builder.setGameUid(loginDataS2S.getGameUid());
        builder.setIsNewRole(loginDataS2S.getIsNewRole());
        builder.setServerTime(System.currentTimeMillis());
        builder.setServerOpenTime(GameEngine.getInstance().getOpenTime());
        builder.setAppUpdatePolicy(loginDataS2S.getAppUpdatePolicy());
        builder.setAppUpdateVersion(loginDataS2S.getAppUpdateVersion());
        builder.setAppUpdateUrl(loginDataS2S.getAppUpdateUrl());
        builder.setGuidePolicy(loginDataS2S.getGuidePolicy());
        builder.setRegisterTime(this.profile.getRegTime());
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            String flag = StringUtil.format("Player@{}_onLogin_{}", this.uid, playerAgent.getClassSimpleName());
            SignalLight.setOn(flag);
            try {
                playerAgent.onLogin(builder);
            } catch (Exception e) {
                log.error("Player On Login Error! uid={}, Agent={}", getUid(), playerAgent.getClassSimpleName(), e);
            }
            SignalLight.setOff(flag);
        }
        builder.setPower(this.powerComponent.calculatePower(true));
        if (loginDataS2S.getIsReconnect()) {
            sendMessage(LoginReconnectRespS2C.newBuilder().setLoginRespS2C(builder).build());
        } else {
            sendMessage(builder.build());
        }
        this.pushComponent.onLoginPush();
        return null;
    }

    public Player(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        this.uid = loginDataS2S.getGameUid();
        this.playerSession = playerSession;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
        this.playerData = new PlayerData(this.uid);
        PlayerEventsInitializer.INITIALIZER.initEvents(this);
        agentAdapterList = ImmutableList.copyOf(PlayerAgentsInitializer.INITIALIZER.initAgents(this));
        agentAdapterList.forEach(PlayerAgentAdapter::initSignalFlags);
        this.loginDataS2S = loginDataS2S;
        if (loginDataS2S.getIsNewRole()) {
            this.state.updateAndGet(s -> PlayerState.registering);
        } else {
            this.playerData.asyncLoad(data -> log.info(""), data -> log.info(""));
            this.state.updateAndGet(s -> PlayerState.loading);
        }
    }

    public boolean isPlayerThread() {
        return Thread.currentThread() == PlayerManager.getInstance().getPlayerGroup(uid);
    }

    public void submit(Runnable runnable) {
        if (isPlayerThread()) {
            runnable.run();
        } else {
            asyncRunner.submit(runnable);
        }
    }

    public PlayerData save() {
        this.requirePlayerThread();
        PlayerData temp = new PlayerData(uid);
        temp.setLoadOver(true);
        temp.setLoadSuccess(true);
        temp.setProfile(this.profile);
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            SignalLight.watch(playerAgent.SIGNAL_FLAG_SAVE, temp, playerAgent::saveData);
        }
        temp.asyncUpdate(
                () -> log.debug("Player Save Success.uid={}", getUid()),
                () -> log.info("Player Save Fail! uid={}", getUid())
        );
        PlayerSnapshotUtil.updateSnapshot(this);
        this.ghostAgent.update(temp);
        return temp;
    }

    void tick() {
        switch (state.get()) {
            case running:
                run();
                break;
            case loading:
                load();
                break;
            case registering:
                register();
                break;
            default:
                log.error("Player Tick");
                break;
        }
    }

    private final RunSignal tickSignal = new RunSignal(TimeUnit.SECONDS.toMillis(1));

    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));

    private void run() {
        if (tickSignal.signal()) {
            for (PlayerAgentAdapter playerAgent : agentAdapterList) {
                SignalLight.watch(playerAgent.SIGNAL_FLAG_TICK, playerAgent::tick);
            }
        }
        int count = 0;
        while (true) {
            if (++count > 10) {
                break;
            }
            PlayerSession atomic = playerSession;
            if (atomic == null) {
                break;
            }
            FramePlayerMessage framePlayerMessage = atomic.pollRecvMessage();
            if (framePlayerMessage == null) {
                break;
            }
            handle(framePlayerMessage);
        }
        eventComponent.handleAsyncEvent();
        asyncRunner.run();
        saveSignal.run(this::save);
    }

    private void handle(FramePlayerMessage playerMessage) {
        long start = System.currentTimeMillis();
        Class<? extends Message> protoType = playerMessage.getProtoType();
        PlayerRequestHandler<?> handler = PlayerRequestHandlerSupport.getHandler(protoType);
        if (handler == null) {
            errorProcess(CommonErrorCode.HANDLER_NOT_FOUND, null, playerMessage, handler, start);
        } else {
            AbstractAsyncHandler asyncHandler = AsyncHandlerManager.getAsyncHandler(handler);
            if (asyncHandler == null) {
                onHandle(start, handler, playerMessage);
            } else {
                asyncHandler.async(handler, this, playerMessage);
            }
        }
        if (handler == null || handler.isAck()) {
            sendMessage(ProtocolSupport.buildProtoAckMsg(protoType));
        }
    }

    public void onHandle(long start, PlayerRequestHandler handler, FramePlayerMessage framePlayerMessage) {
        Throwable throwable = null;
        IErrorCode errorCode;
        try {
            errorCode = handler.handle(this, framePlayerMessage.getProto());
        } catch (ServiceException e) {
            throwable = e;
            errorCode = e.getErrorCode();
        } catch (Throwable ex) {
            throwable = ex;
            errorCode = CommonErrorCode.UNKNOWN_ERROR;
        }
        this.errorProcess(errorCode, throwable, framePlayerMessage, handler, start);
    }

    public void errorProcess(IErrorCode errorCode, Throwable throwable, FramePlayerMessage framePlayerMessage, PlayerRequestHandler<?> handler, long start) {
        if (errorCode != null) {
            sendMessage(ProtocolSupport.buildProtoErrorMsg(framePlayerMessage.getProtoType(), errorCode));
        }
    }

    private void load() {
        if (!playerData.isLoadOver()) {
            return;
        }
        if (!playerData.isLoadSuccess()) {
            state.updateAndGet(s -> PlayerState.loadError);
            return;
        }
        this.eventComponent.setThreadId();
        this.profile = this.playerData.getProfile();
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            try {
                SignalLight.watch(playerAgent.SIGNAL_FLAG_LOAD, playerData, playerAgent::loadData);
            } catch (Exception e) {
                state.updateAndGet(s -> PlayerState.loadError);
            }
        }
        PlayerState ps = state.updateAndGet(prev -> prev == PlayerState.loading ? PlayerState.running : prev);
        if (ps == PlayerState.running) {
            this.onLoadEnd();
            this.playerData = null;
        }
    }

    private void onLoadEnd() {
        PlayerSnapshotUtil.updateSnapshot(this);
        this.powerComponent.calculatePower(true);
    }

    private void calculateOnlineSeconds() {
        long lastLoginTime = this.profile.getLastLoginTime();
        long onlineMillis = System.currentTimeMillis() - lastLoginTime;
        if (onlineMillis > 0L) {
            int onlineSeconds = (int) (onlineMillis / 1000L);
            long totalOnlineSeconds = onlineSeconds + this.profile.getOnlineTime();
            this.profile.setOnlineTime(totalOnlineSeconds);
        }
    }

    private void register() {
        try {

            this.eventComponent.setThreadId();
            this.profile = this.playerData.onRegister(this.loginDataS2S);
        } catch (Exception e) {
            state.updateAndGet(s -> PlayerState.registerError);
            return;
        }
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            try {
                SignalLight.watch(playerAgent.SIGNAL_FLAG_REGISTER, playerData, playerAgent::initData);
            } catch (Exception e) {
                state.updateAndGet(s -> PlayerState.registerError);
            }
        }
        PlayerState ps = state.updateAndGet(prev -> prev == PlayerState.registering ? PlayerState.running : prev);
        if (ps == PlayerState.running) {
            this.playerData.asyncInsert(
                    () -> log.info("Player Register Success,uid={}", getUid()),
                    () -> log.info("Player Register Fail !uid={}", getUid())
            );
            this.onRegisterEnd(this.loginDataS2S);
            this.playerData = null;
        }
    }

    private void onRegisterEnd(LoginDataS2S loginDataS2S) {
        PlayerSnapshotUtil.updateSnapshot(this);
    }

    public void requirePlayerThread() {
        if (!isPlayerThread()) throw new ConcurrentModificationException();
    }

    public PlayerSession setPlayerSession(PlayerSession playerSession) {
        PlayerSession prev = this.playerSession;
        this.playerSession = playerSession;
        return prev;
    }

    public void setSkipLoadTimeCheckOnce() {
        this.skipLoadTimeCheckOnce = true;
    }

    public void onNewDay() {
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            String flag = StringUtil.format("Player@{}_onNewDay_{}", this.uid, playerAgent.getClassSimpleName());
            SignalLight.setOn(flag);
            try {
                playerAgent.onNewDay();
            } catch (Exception e) {
                log.error("Player On New Day Error! uid={}, Agent={}", getUid(), playerAgent.getClassSimpleName(), e);
            }
            SignalLight.setOff(flag);
        }
        if (isOnline()) {
            this.profile.setLastLoginTime(System.currentTimeMillis());
            this.profile.setLastOnlineTime(System.currentTimeMillis());
            this.statisticsComponent.addOneStatisticsInt(StatisticsEnum.LOGIN_COUNT);
            this.eventComponent.postPlayerEvent(PlayerLogin.builder().build());
            this.eventComponent.postPlayerEvent(PlayerFirstLogin.builder().build());
        }
    }

    public void onNewWeek() {

    }

    public void sendMessage(Message message) {
        PlayerSession atomic = playerSession;
        if (atomic != null) {
            if (atomic.send(message)) {
            }
        }
    }

    public void disconnect(IErrorCode errorCode) {
        PlayerSession atomic = this.playerSession;
        if (atomic != null)
            atomic.disconnect(errorCode);
    }

    public IErrorCode onLogout(LogoutReqC2S logoutReqC2S) {
        for (PlayerAgentAdapter playerAgent : agentAdapterList) {
            String flag = StringUtil.format("Player@{}_onLogout_{}", this.uid, playerAgent.getClassSimpleName());
            SignalLight.setOn(flag);
            try {
                playerAgent.onLogout();
            } catch (Exception e) {
                log.error("Player On Logout Error! uid={}, Agent={}", getUid(), playerAgent.getClassSimpleName(), e);
            }
            SignalLight.setOff(flag);
        }
        this.profile.setLastOnlineTime(System.currentTimeMillis());
        save();
        PlayerCaches.snapShot().logout(getUid());
        return null;
    }

    public boolean isOnline() {
        PlayerSession atomic = this.playerSession;
        return (atomic != null && atomic.isOnline());
    }

    public long getDailyResetTime() {
        return NewDay.getDayZeroMillis();
    }
}
