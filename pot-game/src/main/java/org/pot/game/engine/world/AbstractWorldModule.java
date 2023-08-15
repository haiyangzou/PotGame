package org.pot.game.engine.world;

import java.util.concurrent.CompletableFuture;

public class AbstractWorldModule implements WorldModule {

    @Override
    public CompletableFuture<?> init() {
        return null;
    }

    @Override
    public void initPlayerData() {

    }

    @Override
    public void onDailyReset(long resetTime) {

    }

    @Override
    public void onWeeklyReset(long resetTime) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void tick() {

    }
}
