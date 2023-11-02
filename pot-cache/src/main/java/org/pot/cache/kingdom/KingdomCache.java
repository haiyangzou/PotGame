package org.pot.cache.kingdom;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.union.UnionSnapshotCache;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.structure.CacheMap;
import org.pot.common.util.JsonUtil;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KingdomCache {
    private static volatile KingdomCache instance;

    public synchronized static void init(KingdomCacheConfig cacheConfig, ReactiveStringRedisTemplate redisTemplate) {
        if (instance == null) {
            instance = new KingdomCache(cacheConfig, redisTemplate);
        }
    }

    public synchronized static void shutdown() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public static KingdomCache instance() {
        return instance;
    }

    private final LoadingCache<Integer, Kingdom> cache;
    public final KingdomCacheConfig cacheConfig;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final AsyncExecutor executor;
    private final Map<Integer, Long> loadTimeMap;
    private final ScheduledFuture<?> refreshFuture;

    public KingdomCache(KingdomCacheConfig cacheConfig, ReactiveStringRedisTemplate redisTemplate) {
        this.cacheConfig = cacheConfig;
        this.redisTemplate = redisTemplate;
        this.cache = Caffeine.newBuilder()
                .recordStats()
                .maximumSize(cacheConfig.getMaxSize())
                .initialCapacity(cacheConfig.getMaxSize())
                .refreshAfterWrite(cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS)
                .build(this::loadKingdomSnapShot);
        this.loadTimeMap = CacheMap.of(cacheConfig.getMaxSize());
        this.executor = new AsyncExecutor(UnionSnapshotCache.class.getSimpleName(), cacheConfig.getThreads());
        this.refreshFuture = this.executor.scheduleAtFixedRate(new CacheRefresher(),
                cacheConfig.getRefreshSeconds(), cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS);

    }

    private Kingdom loadKingdomSnapShot(int sid) {
        if (sid <= 0) return null;
        loadTimeMap.put(sid, DateTimeUtil.getUnixTimestamp());
        try {
            String kingdomRedisKey = Kingdom.buildRedisKey(sid);
            String kingdomRedisData = redisTemplate.opsForValue().get(kingdomRedisKey).block();
            if (kingdomRedisData == null) return null;
            return JsonUtil.parseJackJson(kingdomRedisData, Kingdom.class);
        } catch (Throwable throwable) {
            log.error("load KingdomInfo error sid={}", sid, throwable);
        }
        return null;
    }

    public void refreshKingdom(int sid) {
        boolean present = cache.getIfPresent(sid) != null;
        cache.invalidate(sid);
        if (present) {
            cache.refresh(sid);
        }
    }

    private void close() {
        this.refreshFuture.cancel(false);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, refreshFuture::isDone);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, executor::isIdle);
        this.executor.shutdown();
    }

    private final class CacheRefresher implements Runnable {
        @Override
        public void run() {
            final long nowTime = DateTimeUtil.getUnixTimestamp();
            ImmutableSet<Integer> ids = ImmutableSet.copyOf(cache.asMap().keySet());
            for (Integer id : ids) {
                long loadTime = loadTimeMap.getOrDefault(id, 0L);
                if (nowTime - loadTime > cacheConfig.getRefreshSeconds() - 1) {
                    cache.refresh(id);
                }
            }
        }
    }
}
