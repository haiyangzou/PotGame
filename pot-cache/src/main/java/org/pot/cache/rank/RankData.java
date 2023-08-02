package org.pot.cache.rank;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.pot.cache.player.PlayerCaches;
import org.pot.cache.rank.codec.RankItemCodec;
import org.pot.cache.union.UnionCaches;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.RunSignal;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class RankData {
    private static final String RANK_DATA_REDIS_PREFIX = "RANK:DATA:";
    private static final String RANK_EXTRA_DATA_REDIS_PREFIX = "RANK:EXTRA:DATA:";
    private final String rankKey;
    private final String rankExtraKey;
    private final long maxSize;
    private final boolean rankRate;
    private final boolean localRank;
    private final boolean unionRank;
    private final RankItemCodec codec;
    private volatile boolean rankDeleting;
    private final RankCache rankCache;
    private final RunSignal loadSignal;
    private final RunSignal truncateSignal;
    private final ScheduledFuture<?> flusherFuture;
    final ChangeRankAndLoadFlusher flusher;
    private final Semaphore flushSemaphore = new Semaphore(1);
    private final Set<Long> rankInfoDeleting = Sets.newConcurrentHashSet();
    private final List<RankItem> rankInfoList = new CopyOnWriteArrayList<>();
    private final Set<Long> changedRankItemSet = Sets.newConcurrentHashSet();
    private final NavigableMap<Long, RankItem> changedRankItemMap = new ConcurrentSkipListMap<>();
    private final AtomicReference<List<ZSetOperations.TypedTuple<String>>> redisRankItems = new AtomicReference<>(null);

    public RankData(RankCache rankCache, String rankKey, RankItemCodec codec, long maxSize, boolean localRank, boolean unionRank, boolean rankRate) {
        this.rankKey = rankKey;
        this.maxSize = maxSize <= 0 ? 300000 : Math.min(maxSize, 300000);
        this.rankRate = rankRate;
        this.localRank = localRank;
        this.unionRank = unionRank;
        this.codec = codec;
        this.rankCache = rankCache;
        this.rankExtraKey = RANK_EXTRA_DATA_REDIS_PREFIX + rankKey;
        this.loadSignal = new RunSignal(true, TimeUnit.SECONDS.toMillis(rankCache.cacheConfig.getLoadSeconds()));
        this.truncateSignal = new RunSignal(true, TimeUnit.SECONDS.toMillis(rankCache.cacheConfig.getTruncateSeconds()));
        this.flusher = new ChangeRankAndLoadFlusher(true);
        this.flusherFuture = rankCache.executor.scheduleAtFixedRate(flusher, 1, 1, TimeUnit.SECONDS);
        this.loadRedisRankItems(true);//首次加载Redis的排行榜
    }

    private void loadRedisRankItems(boolean first) {
        List<ZSetOperations.TypedTuple<String>> tempRedisRankItems = redisRankItems.getAndUpdate(rankItems -> null);
        //所有排行榜本地玩家必须实时变化
        if (CollectionUtil.isNotEmpty(tempRedisRankItems)) {
            List<RankItem> nonCHangedRankItems = Lists.newArrayListWithExpectedSize(tempRedisRankItems.size());
            for (ZSetOperations.TypedTuple<String> tuple : tempRedisRankItems) {
                RankItem rankItem = codec.decode(tuple);
                if (rankItem == null) {
                    rankCache.redisTemplate.opsForZSet().remove(rankKey, tuple.getValue()).subscribe();
                    rankCache.redisTemplate.opsForHash().remove(rankExtraKey, tuple.getValue()).subscribe();
                    continue;
                }
                if (!changedRankItemSet.contains(rankItem.getUuid())) {
                    nonCHangedRankItems.add(rankItem);
                }
                if (unionRank) {
                    rankCache.executor.execute(() -> UnionCaches.snapshot().getSnapshot(rankItem.getUuid()));
                } else {
                    rankCache.executor.execute(() -> PlayerCaches.snapShot().getSnapshot(rankItem.getUuid()));
                }
            }
        }
    }

    final class ChangeRankAndLoadFlusher implements Runnable {
        private volatile boolean alive;

        public ChangeRankAndLoadFlusher(boolean alive) {
            this.alive = alive;
        }

        @Override
        public void run() {

        }
    }
}
