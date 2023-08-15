package org.pot.game.engine.world;

import org.pot.common.function.Ticker;

import java.util.concurrent.CompletableFuture;

public interface WorldModule extends Ticker {
    CompletableFuture<?> init();

    void initPlayerData();

    void onDailyReset(long resetTime);

    void onWeeklyReset(long resetTime);

    void shutdown();

}
