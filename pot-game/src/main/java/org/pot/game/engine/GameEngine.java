package org.pot.game.engine;

import lombok.Getter;
import org.pot.common.date.DateTimeUtil;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.rank.RankManager;
import org.pot.game.engine.switchcontrol.SwitchManager;
import org.pot.game.gate.GameConnManager;
import org.pot.game.gate.GhostUtil;
import org.pot.game.gate.TunnelManager;
import org.pot.game.persistence.GameDb;
import org.pot.game.resource.GameConfigSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GameEngine extends AppEngine<GameEngineConfig> {
    @Getter
    private NettyClientEngine<FramePlayerMessage> nettyClientEngine;
    @Getter
    private NettyServerEngine<FramePlayerMessage> nettyServerEngine;
    @Getter
    private GameConnManager gameConnManager;

    public static GameEngine getInstance() {
        return (GameEngine) EngineInstance.getInstance();
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

    @Override
    protected void doStart() throws Throwable {
        GameDb.init(getConfig());
        GameConfigSupport.init(null);
        ReactiveRedis.init(getConfig().getLocalRedisConfig(), getConfig().getGlobalRedisConfig(), getConfig().getRankRedisConfig());
        SwitchManager.getInstance().init();
        PlayerManager.getInstance().init();
        RankManager.getInstance().init();
        WorldManager.getInstance().init();
        GhostUtil.load();
        SwitchManager.getInstance().startup();
        TunnelManager.init();
    }

    @Override
    protected void doStop() {

    }
}
