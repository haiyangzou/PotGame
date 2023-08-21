package org.pot.cache.union;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.MapUtil;
import org.pot.dal.redis.RedisKey;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
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
                .maximumSize(cacheConfig.getMaxMemberSize())
                .initialCapacity(cacheConfig.getMaxMemberSize())
                .refreshAfterWrite(cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS)
                .expireAfterAccess(cacheConfig.getExpireSeconds(), TimeUnit.SECONDS)
                .build(this::loadUnionMemberSnapShot);
        this.executor = new AsyncExecutor(UnionSnapshotCache.class.getSimpleName(), cacheConfig.getThreads());
        this.flusherFuture = this.executor.scheduleAtFixedRate(new CacheRefresher(),
                cacheConfig.getFlushSecons(), cacheConfig.getFlushSecons(), TimeUnit.SECONDS);
    }

    private UnionSnapshot loadUnionSnapShot(long unionId) {
        if (unionId <= 0) return null;
        unionLoadTimeMap.put(unionId, DateTimeUtil.getUnixTimestamp());
        try {
            String unionRedisKey = RedisKey.union.build(unionId);
            String unionRedisData = redisTemplate.opsForValue().get(unionRedisKey).block();
            if (unionRedisData == null) return null;
            Map<String, String> union = JsonUtil.parseHashMap(unionRedisData, String.class, String.class);
            String unionMemberIdsRedisKey = RedisKey.union_member_ids.build(unionId);
            Set<Long> members = redisTemplate.opsForSet().members(unionMemberIdsRedisKey).map(Long::parseLong).collect(Collectors.toSet()).blockOptional().orElse(Collections.emptySet());
            String techRedisKey = RedisKey.union_tech.build(unionId);
            String techRedisData = redisTemplate.opsForValue().get(techRedisKey).block();
            Map<String, String> unionTechData = JsonUtil.parseHashMap(techRedisData, String.class, String.class);
            return new UnionSnapshot(union, members, unionTechData);
        } catch (Throwable throwable) {
            log.error("load UnionSnapshot error.uid={}", unionId, throwable);
        }
        return null;
    }

    private UnionMemberSnapshot loadUnionMemberSnapShot(long unionMemberId) {
        if (unionMemberId <= 0) return null;
        memberLoadTimeMap.put(unionMemberId, DateTimeUtil.getUnixTimestamp());
        try {
            String memberRedisKey = RedisKey.union_member.build(unionMemberId);
            String memberRedisData = redisTemplate.opsForValue().get(memberRedisKey).block();
            Map<String, String> member = JsonUtil.parseHashMap(memberRedisData, String.class, String.class);
            if (MapUtil.isEmpty(member)) return null;
            return new UnionMemberSnapshot(member);
        } catch (Throwable throwable) {
            log.error("load UnionMemberSnapshot error", throwable);
        }
        return null;
    }

    public void refreshUnionSnapshot(long unionId) {
        cache.invalidate(unionId);
        cache.refresh(unionId);
    }

    public void refreshUnionMemberSnapshot(long unionId) {
        memberCache.invalidate(unionId);
        memberCache.refresh(unionId);
    }

    public UnionSnapshot getSnapshot(long unionId) {
        long nowTime = DateTimeUtil.getUnixTimestamp();
        long loadTime = unionLoadTimeMap.getOrDefault(unionId, 0L);
        if (nowTime - loadTime > cacheConfig.getRefreshSeconds()) {
            cache.invalidate(unionId);
        }
        return unionId <= 0 ? null : cache.get(unionId);
    }

    public UnionMemberSnapshot getUnionMemberSnapshot(long unionMemberId) {
        long nowTime = DateTimeUtil.getUnixTimestamp();
        long loadTime = memberLoadTimeMap.getOrDefault(unionMemberId, 0L);
        if (nowTime - loadTime > cacheConfig.getRefreshSeconds()) {
            memberCache.invalidate(unionMemberId);
        }
        return unionMemberId <= 0 ? null : memberCache.get(unionMemberId);
    }

    public UnionSnapshot getPlayerUnionSnapshot(long playerId) {
        if (playerId <= 0) return null;
        UnionMemberSnapshot unionMemberSnapshot = getUnionMemberSnapshot(playerId);
        if (unionMemberSnapshot == null) return null;
        UnionSnapshot unionSnapshot = getSnapshot(unionMemberSnapshot.getUnionId());
        if (unionSnapshot == null) return null;
        return unionSnapshot.isMember(playerId) ? unionSnapshot : null;
    }

    public long getPlayerUnionId(long playerId) {
        UnionSnapshot unionSnapshot = getPlayerUnionSnapshot(playerId);
        return unionSnapshot == null ? 0 : unionSnapshot.getUnionId();
    }

    private void close() {
        this.flusherFuture.cancel(false);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, flusherFuture::isDone);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, executor::isIdle);
        this.executor.shutdown();
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
