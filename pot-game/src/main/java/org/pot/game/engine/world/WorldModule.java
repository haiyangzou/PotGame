package org.pot.game.engine.world;

import org.pot.common.function.Ticker;

public interface WorldModule extends Ticker {
    void init();

    void initPlayerData();

    void onDailyReset(long resetTime);

    void onWeeklyReset(long resetTime);

    void shutdown();

}
