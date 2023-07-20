package org.pot.game.engine;

import lombok.Getter;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;

public class GameEngine extends AppEngine<GameEngineConfig> {
    @Getter
    private NettyClientEngine<FramePlayerMessage> nettyClientEngine;
    @Getter
    private NettyServerEngine<FramePlayerMessage> nettyServerEngine;

    public static GameEngine getInstance() {
        return (GameEngine) EngineInstance.getInstance();
    }

    protected GameEngine(Class<GameEngineConfig> configClass) throws Exception {
        super(configClass);
    }
}
