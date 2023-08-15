package org.pot.game.engine;

import lombok.Getter;
import org.pot.common.Constants;
import org.pot.core.engine.EngineConfig;

@Getter
public class GameEngineConfig extends EngineConfig {
    private long gateTIckIntervalMillis = Constants.RUN_INTERVAL_MS;
    private long worldTickIntervalMillis = Constants.RUN_INTERVAL_MS;
    private long playerTickIntervalMillis = Constants.RUN_INTERVAL_MS;
    private long playerSessionRecvQueueMaxSize = 500;

}
