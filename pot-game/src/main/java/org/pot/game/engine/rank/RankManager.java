package org.pot.game.engine.rank;

import lombok.Getter;
import org.pot.cache.rank.RankCache;
import org.pot.cache.rank.RankCacheConfig;
import org.pot.common.task.PeriodTask;
import org.pot.common.util.DateTimeUnit;
import org.pot.common.util.ExDateTimeUtil;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.RankType;

public class RankManager implements PeriodTask {
    @Getter
    private static final RankManager instance = new RankManager();
    private static RankCache rankCache;
    private long lastTime = System.currentTimeMillis();

    public void init() {
        RankCacheConfig rankCacheConfig = GameEngine.getInstance().getConfig().getRankCacheConfig();
        rankCache = new RankCache(rankCacheConfig, ReactiveRedis.rank());
        int rankMaxSize = 0;
        for (RankType rankType : RankType.values()) {
            rankCache.register(RankType.getRankKey(rankType.getRedisKey(), true), rankType.getCodec(), rankMaxSize, false, rankType.isUnion(), false);
        }
    }

    public void close() {
        RankCache temp = rankCache;
        if (temp != null) {
            temp.close();
        }
    }

    @Override
    public void doPeriodicTask() {
        GameEngine.getInstance().getAsyncExecutor().execute(() -> {
            if (!ExDateTimeUtil.isSameDay(lastTime, System.currentTimeMillis())) {
                //TODO
            }
            lastTime = System.currentTimeMillis();
        });
    }

    @Override
    public DateTimeUnit getDateTimeUnit() {
        return DateTimeUnit.DAY;
    }
}
