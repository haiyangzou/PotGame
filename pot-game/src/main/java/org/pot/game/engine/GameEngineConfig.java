package org.pot.game.engine;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.pot.cache.player.PlayerCacheConfig;
import org.pot.cache.rank.RankCacheConfig;
import org.pot.common.Constants;
import org.pot.common.communication.server.ServerType;
import org.pot.core.engine.EngineConfig;

@Getter
public class GameEngineConfig extends EngineConfig {
    private ServerType serverType = ServerType.GAME_SERVER;
    private long gateTIckIntervalMillis = Constants.RUN_INTERVAL_MS;
    private long worldTickIntervalMillis = Constants.RUN_INTERVAL_MS;
    private long playerTickIntervalMillis = Constants.RUN_INTERVAL_MS;
    private long playerSessionRecvQueueMaxSize = 500;
    private long playerSessionSendQueueMaxSize = 500;
    private RankCacheConfig rankCacheConfig;
    private PlayerCacheConfig playerCacheConfig;
    private int playerGroupSize = 10;

    @Override
    protected void setProperties(Configuration config) {

    }
}
