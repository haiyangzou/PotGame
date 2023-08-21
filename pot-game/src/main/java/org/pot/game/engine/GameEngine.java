package org.pot.game.engine;

import lombok.Getter;
import org.pot.common.date.DateTimeUtil;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;
import org.pot.game.gate.GameConnManager;

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

    }

    @Override
    protected void doStop() {

    }
}
