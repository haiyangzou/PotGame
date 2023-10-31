package org.pot.cache.rank;

import org.pot.cache.rank.codec.RankItemCodec;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RankCache {
    final RankCacheConfig cacheConfig;
    final ReactiveStringRedisTemplate redisTemplate;
    final AsyncExecutor executor;
    final Map<String, RankData> rankDataMap = new ConcurrentHashMap<>();

    public RankCache(RankCacheConfig cacheConfig, ReactiveStringRedisTemplate redisTemplate) {
        this.cacheConfig = cacheConfig;
        this.redisTemplate = redisTemplate;
        this.executor = new AsyncExecutor(RankCache.class.getSimpleName(), cacheConfig.getThreads());
    }

    public RankData register(String rankKey, RankItemCodec codec, int maxSize, boolean localRank, boolean unionRank, boolean rankRate) {
        return rankDataMap.computeIfAbsent(rankKey, s -> new RankData(this, rankKey, codec, maxSize, localRank, unionRank, rankRate));
    }

    public void unregister(String rankKey) {
        RankData rankData = rankDataMap.remove(rankKey);
        if (rankData != null) {
            rankData.flusherFuture.cancel(false);
            executor.execute(rankData.flusher.shutdown());
        }
    }

    public RankData getRankData(String rankKey) {
        return rankDataMap.get(rankKey);
    }

    public void close() {
        rankDataMap.values().forEach(rankData -> rankData.flusherFuture.cancel(false));
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> rankDataMap.values().stream().allMatch(rankData -> rankData.flusherFuture.isDone()));
        rankDataMap.values().forEach(rankData -> executor.execute(rankData.flusher.shutdown()));
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, executor::isIdle);
        this.executor.shutdown();
    }
}
