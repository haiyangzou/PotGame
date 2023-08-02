package org.pot.cache.union;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.pot.cache.player.PlayerCacheConfig;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.cache.player.snapshot.PlayerSnapShotCache;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.common.date.DateTimeUtil;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class UnionSnapshotCache {
    private final LoadingCache<Long, UnionSnapshot> cache;
    private final LoadingCache<Long, UnionMemberSnapshot> memberCache;
    private final UnionCacheConfig cacheConfig;
    private final AsyncExecutor executor;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ScheduledFuture<?> flusherFuture;
    private final Map<Long, Long> unionLoadTimeMap = new ConcurrentHashMap<>();
    private final Map<Long, Long> memberLoadTimeMap = new ConcurrentHashMap<>();

    private final Set<Long> changedSet = Sets.newConcurrentHashSet();
    private final Semaphore flushSemaphore = new Semaphore(1);

    public UnionSnapshot getSnapshot(long uid) {
        return uid <= 0 ? null : cache.get(uid);
    }

    public UnionSnapshotCache(UnionCacheConfig cacheConfig, ReactiveStringRedisTemplate redisTemplate) {
        this.cacheConfig = cacheConfig;
        this.redisTemplate = redisTemplate;
        this.cache = Caffeine.newBuilder()
                .recordStats()
                .maximumSize(cacheConfig.getMaxSize())
                .initialCapacity(cacheConfig.getMaxSize())
                .refreshAfterWrite(cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS)
                .expireAfterAccess(cacheConfig.getExpireSeconds(), TimeUnit.SECONDS)
                .build(this::loadUnionSnapShot);
        this.memberCache = Caffeine.newBuilder()
                .recordStats()
                .maximumSize(cacheConfig.getMaxSize())
                .initialCapacity(cacheConfig.getMaxSize())
                .refreshAfterWrite(cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS)
                .expireAfterAccess(cacheConfig.getExpireSeconds(), TimeUnit.SECONDS)
                .build(this::loadUnionMemberSnapShot);
        this.executor = new AsyncExecutor(UnionSnapshotCache.class.getSimpleName(), cacheConfig.getThreads());
        this.flusherFuture = this.executor.scheduleAtFixedRate(new CacheRefresher(),
                cacheConfig.getFlushSecons(), cacheConfig.getFlushSecons(), TimeUnit.SECONDS);
    }

    private UnionSnapshot loadUnionSnapShot(long unionId) {
        return null;
    }

    private UnionMemberSnapshot loadUnionMemberSnapShot(long unionId) {
        return null;
    }

    private final class CacheRefresher implements Runnable {
        @Override
        public void run() {
            refresh(cache, unionLoadTimeMap, cache::refresh, "[union snapshot cache stats]");
            refresh(memberCache, memberLoadTimeMap, memberCache::refresh, "[member snapshot cache stats]");

        }

        private void refresh(LoadingCache<Long, ?> cache, Map<Long, Long> loadTimeMap, Consumer<Long> consumer, String label) {
            final long nowTime = DateTimeUtil.getUnixTimestamp();
            ImmutableSet<Long> ids = ImmutableSet.copyOf(cache.asMap().keySet());
            for (Long id : ids) {
                long loadTime = loadTimeMap.getOrDefault(id, 0L);
                if (nowTime - loadTime > cacheConfig.getRefreshSeconds() - 1) {
                    consumer.accept(id);
                }
            }
        }
    }
}
