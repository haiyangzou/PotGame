package org.pot.cache.player.snapshot;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.player.PlayerCacheConfig;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.dal.RedisUtils;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class PlayerSnapShotCache {
    // Add other fields and methods as needed
    public static final String CACHE_REDIS_PREFIX = "PlayerSnapshotId";
    public static Duration CACHE_EXPIRED_DURATION = Duration.ofDays(90);
    private final PlayerCacheConfig cacheConfig;
    private final AsyncExecutor executor;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final LoadingCache<Long, PlayerSnapShot> cache;
    private final Function<Long, PlayerSnapShot> memory;
    private final Function<Long, PlayerSnapShot> database;
    private final ScheduledFuture<?> flusherFuture;
    private final Map<Long, Long> loadTimeMap = new ConcurrentHashMap<>();
    private final Set<Long> changedSet = Sets.newConcurrentHashSet();
    private final Semaphore flushSemaphore = new Semaphore(1);

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
                .expireAfterWrite(CACHE_EXPIRED_DURATION)
                .maximumSize(cacheConfig.getMaxSize())
                .initialCapacity(cacheConfig.getMaxSize())
                .refreshAfterWrite(cacheConfig.getRefreshSeconds(), TimeUnit.SECONDS)
                .expireAfterAccess(cacheConfig.getExpireSeconds(), TimeUnit.SECONDS)
                .build(this::loadPlayerSnapShot);
        this.memory = memory;
        this.database = database;
        this.flusherFuture = this.executor.scheduleAtFixedRate(new ChangeDataFlusher(true),
                cacheConfig.getFlushSeconds(), cacheConfig.getFlushSeconds(), TimeUnit.SECONDS);

    }

    private final class ChangeDataFlusher implements Runnable {
        private final boolean async;

        public ChangeDataFlusher(boolean async) {
            this.async = async;
        }

        @Override
        public void run() {
            try {
                flushSemaphore.acquire();
                long start = System.currentTimeMillis();
                int flushAmount = 0;
                Iterator<Long> iterator = changedSet.iterator();
                while (iterator.hasNext()) {
                    Long uid = iterator.next();
                    PlayerSnapShot playerSnapShot = cache.get(uid);
                    if (playerSnapShot != null) {
                        flushAmount++;
                        putSnapshotToRedis(playerSnapShot, async);
                    }
                    iterator.remove();
                }
                long flushTime = Math.max(0, System.currentTimeMillis() - start);
                log.info("[player snapshot flush stats] cached={} flushed={} use={}ms,changed={}",
                        cache.estimatedSize(), flushAmount, flushTime, changedSet.size());
            } catch (Throwable e) {
                log.error("player snaphot flush error", e);
            } finally {
                flushSemaphore.release();
            }
        }
    }

    private void putSnapshotToRedis(PlayerSnapShot playerSnapShot, boolean async) {
        try {
            String key = keyOf(playerSnapShot.getUid());
            String value = playerSnapShot.toJson();
            if (async) {
                redisTemplate.opsForValue().set(key, value).subscribe();
            } else {
                redisTemplate.opsForValue().set(key, value).block();
            }
        } catch (Throwable e) {
            log.error("player snaphot put to redis error", e);
        }
    }

    private PlayerSnapShot loadPlayerSnapShot(Long playerId) {
        if (playerId < 0)
            return null;
        long nowTime = System.currentTimeMillis();
        long loadTime = loadTimeMap.getOrDefault(playerId, 0L);
        if (nowTime - loadTime < cacheConfig.getRefreshSeconds() - 1) {
            // 上次加載過，過一段時間再加載，防止緩存穿透
            return null;
        }
        loadTimeMap.put(playerId, nowTime);
        PlayerSnapShot playerSnapShot;
        if (memory != null) {
            playerSnapShot = memory.apply(playerId);
            if (playerSnapShot != null) {
                playerSnapShot.setFlushMarker(this::markChanged);
                return playerSnapShot;
            }
        }
        playerSnapShot = loadSnapshotFromRedis(playerId);
        if (playerSnapShot != null) {
            playerSnapShot.setFlushMarker(this::markChanged);
            return playerSnapShot;
        }
        if (database != null) {
            this.executor.execute(() -> {
                PlayerSnapShot databasePlayer = database.apply(playerId);
                if (databasePlayer != null) {
                    putSnapshot(databasePlayer);
                }
            });
        }
        return null;
    }

    public void putSnapshot(PlayerSnapShot playerSnapShot) {
        playerSnapShot.setFlushMarker(this::markChanged);
        cache.put(playerSnapShot.getUid(), playerSnapShot);
        markChanged(playerSnapShot.getUid());
    }

    private void markChanged(Long uid) {
        changedSet.add(uid);
    }

    private PlayerSnapShot loadSnapshotFromRedis(long uid) {
        return redisTemplate.opsForValue().get(keyOf(uid))
                .map(PlayerSnapShot::fromJson).block();
    }

    public static String keyOf(long uid) {
        return RedisUtils.buildRedisKey(CACHE_REDIS_PREFIX, uid);
    }

    private void update(long uid, Consumer<PlayerSnapShot> consumer) {
        getOptionalSnapshot(uid).ifPresent(consumer);
        markChanged(uid);
    }

    public Optional<PlayerSnapShot> getOptionalSnapshot(long uid) {
        return Optional.ofNullable(getSnapshot(uid));
    }

    public PlayerSnapShot getSnapshot(long uid) {
        return uid <= 0 ? null : cache.get(uid);
    }

    public void updateName(long uid, String name) {
        update(uid, o -> o.setAccount(name));
    }

    public void close() {
        this.flusherFuture.cancel(false);
        this.executor.execute(new ChangeDataFlusher(false));
        this.executor.shutdown();
    }
}