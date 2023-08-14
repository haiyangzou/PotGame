package org.pot.game.engine;

import lombok.Getter;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;
import org.pot.game.gate.GameConnManager;

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
}
