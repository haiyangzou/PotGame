package org.pot.game.engine;

import lombok.Getter;
import org.pot.PotPackage;
import org.pot.cache.kingdom.KingdomCache;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.id.UniqueIdUtil;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.engine.IHttpServer;
import org.pot.core.net.netty.FramePlayerCodec;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.game.engine.log.LogManager;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.rank.RankManager;
import org.pot.game.engine.switchcontrol.SwitchManager;
import org.pot.game.engine.world.WorldManager;
import org.pot.game.gate.GameConnManager;
import org.pot.game.gate.GhostUtil;
import org.pot.game.gate.TunnelManager;
import org.pot.game.persistence.GameDb;
import org.pot.game.resource.GameConfigSupport;
import org.pot.web.JettyHttpServer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class GameEngine extends AppEngine<GameEngineConfig> {
    @Getter
    private NettyClientEngine<FramePlayerMessage> nettyClientEngine;
    @Getter
    private NettyServerEngine<FramePlayerMessage> nettyServerEngine;
    @Getter
    private GameConnManager gameConnManager;

    @Getter
    private IHttpServer httpServer;

    public static GameEngine getInstance() {
        return (GameEngine) EngineInstance.getInstance();
    }

    public GameEngine() throws Exception {
        super(GameEngineConfig.class);
    }

    protected GameEngine(Class<GameEngineConfig> configClass) throws Exception {
        super(configClass);
    }

    public long getOpenTime() {
        return GameServerInfo.getOpenTimestamp();
    }

    public int getOpenedDays() {
        if (getOpenTime() > System.currentTimeMillis()) {
            return 0;
        }
        return DateTimeUtil.differentDays(getOpenDateTime());
    }

    public LocalDate getOpenDate() {
        return getOpenDateTime().toLocalDate();
    }

    public LocalDateTime getOpenDateTime() {
        return DateTimeUtil.toLocalDateTime(getOpenTime());
    }

    public long nextId() {
        return UniqueIdUtil.newUniqueId(GameServerInfo.getServerIdObject());
    }

    @Override
    protected void doStart() throws Throwable {
        GameServerInfo.init();
        LogManager.init(PotPackage.class);
        GameDb.init(getConfig());
        GameConfigSupport.init(null);
        GamePeriodicTasks.init(this);
        ReactiveRedis.init(getConfig().getLocalRedisConfig(), getConfig().getGlobalRedisConfig(), getConfig().getRankRedisConfig());
        KingdomCache.init(getConfig().getKingdomCacheConfig(), ReactiveRedis.global());
        SwitchManager.getInstance().init();
        PlayerManager.getInstance().init();
        RankManager.getInstance().init();
        WorldManager.getInstance().init();
        GhostUtil.load();
        SwitchManager.getInstance().startup();
        initHttpServer();
        initNettyClientEngine();
        initNettyServerEngine();
        TunnelManager.init();
    }

    private void initHttpServer() {
        httpServer = new JettyHttpServer(getConfig().getJettyConfig());
        httpServer.startup();
    }

    private void initNettyClientEngine() {
        this.nettyClientEngine = new NettyClientEngine<>(getConfig(), FramePlayerCodec::new);
        this.nettyServerEngine.start();
    }

    private void initNettyServerEngine() {
        this.gameConnManager = new GameConnManager();
        this.nettyServerEngine = new NettyServerEngine<>(getConfig(), FramePlayerCodec::new);
        this.nettyServerEngine.getConnectionManager().setListener(gameConnManager);
        this.nettyServerEngine.start();
    }

    @Override
    protected void doStop() {
        TunnelManager.shutdown();
        if (nettyServerEngine != null) {
            nettyServerEngine.stop();
        }
        if (nettyClientEngine != null) {
            nettyClientEngine.stop();
        }
        if (gameConnManager != null) {
            gameConnManager.close();
        }
        PlayerManager.getInstance().waitingForAddMaintainShield();
        PlayerManager.getInstance().waitingForConsumeReceivedRequest();
        SwitchManager.getInstance().shutdown();
        WorldManager.getInstance().close();
        PlayerManager.getInstance().close();
        RankManager.getInstance().close();
        KingdomCache.shutdown();
        GameServerInfo.shutdown();
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> getAsyncExecutor().isIdle());
        ReactiveRedis.close();
        GameDb.close();
    }
}
