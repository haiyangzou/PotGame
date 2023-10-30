package org.pot.game.engine.rank;

import lombok.Getter;
import org.pot.cache.rank.RankCache;
import org.pot.cache.rank.RankCacheConfig;
import org.pot.common.task.PeriodTask;
import org.pot.common.util.DateTimeUnit;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.game.engine.GameEngine;

public class RankManager implements PeriodTask {
    @Getter
    private static final RankManager instance = new RankManager();
    private static RankCache rankCache;
    private long lastTime = System.currentTimeMillis();

    public void init() {
        RankCacheConfig rankCacheConfig = GameEngine.getInstance().getConfig().getRankCacheConfig();
        rankCache = new RankCache(rankCacheConfig, ReactiveRedis.rank());
    }

    @Override
    public void doPeriodicTask() {

    }

    @Override
    public DateTimeUnit getDateTimeUnit() {
        return null;
    }
}
