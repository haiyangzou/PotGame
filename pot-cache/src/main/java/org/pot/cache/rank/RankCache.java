package org.pot.cache.rank;

import org.pot.cache.rank.codec.RankItemCodec;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public RankData getRankData(String rankKey) {
        return rankDataMap.get(rankKey);
    }
}
