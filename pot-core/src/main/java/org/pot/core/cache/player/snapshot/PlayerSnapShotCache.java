package org.pot.core.cache.player.snapshot;

import org.pot.core.cache.player.PlayerCacheConfig;
import org.pot.core.common.concurrent.executor.AsyncExecutor;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Caffeine;
import org.springframework.boot.autoconfigure.web.ResourceProperties.Cache.Cachecontrol;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class PlayerSnapShotCache {
    // Add other fields and methods as needed
    public static final String CACHE_REDIS_PREFIX = "PlayerSnapshotId";
    public static Duration CACHE_EXPRIED_DURATION = Duration.ofDays(90);
    private final PlayerCacheConfig cacheConfig;
    private final AsyncExecutor executor;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final LoadingCache<Long, PlayerSnapShot> cache;
    private final Function<Long, PlayerSnapShot> memory;
    private final Function<Long, PlayerSnapShot> database;
    private final ScheduledFuture<?> flusherFuture;

    public PlayerSnapShotCache(PlayerCacheConfig cacheConfig,
            ReactiveStringRedisTemplate redisTemplate,
            Function<Long, PlayerSnapShot> memory,
            Function<Long, PlayerSnapShot> database) {
        // Initialize the fields with the provided parameters
        this.cacheConfig = cacheConfig;
        this.executor = new AsyncExecutor(PlayerSnapShot.class.getSimpleName(), cacheConfig.getThreads());
        this.redisTemplate = redisTemplate;
        this.cache = com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(CACHE_EXPRIED_DURATION)
                .maximumSize(cacheConfig.getMaxSize())
                .initialCapacity(cacheConfig.getMaxSize())
                .refreshAfterWrite(cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS)
                .expireAfterAccess(cacheConfig.getExpireSeconds(), TimeUnit.SECONDS)
                .build(this::loadPlayerSnapShot);
        this.memory = memory;
        this.database = database;
        this.flusherFuture = this.executor.scheduleAtFixedRate(new ChangeDataFlusher(true),
                cacheConfig.getFlushSecons(), cacheConfig.getFlushSecons(), TimeUnit.SECONDS);

    }

    private final class ChangeDataFlusher implements Runnable {
        private final boolean async;

        public ChangeDataFlusher(boolean async) {
            this.async = async;
        }

        @Override
        public void run() {

        }
    }

    private PlayerSnapShot loadPlayerSnapShot(long playerId) {
        if (playerId < 0)
            return null;
        return null;
    }
}