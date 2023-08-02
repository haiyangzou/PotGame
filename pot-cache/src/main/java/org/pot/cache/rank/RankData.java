package org.pot.cache.rank;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.pot.cache.player.PlayerCaches;
import org.pot.cache.rank.codec.RankItemCodec;
import org.pot.cache.union.UnionCaches;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.MapUtil;
import org.pot.common.util.RunSignal;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
            for (RankItem nonCHangedRankItem : nonCHangedRankItems) {
                rankCache.redisTemplate.opsForHash().get(rankExtraKey, Long.toString(nonCHangedRankItem.getUuid())).doOnSuccess(nonCHangedRankItem::parseExtraData).subscribe();
            }
            putMemoryRank(nonCHangedRankItems);
        }
        if (localRank && !first) {
            return;
        }
        if (loadSignal.signal() || first) {
            rankCache.redisTemplate.opsForZSet().reverseRangeWithScores(rankKey, Range.closed(0L, maxSize)).collectList().doOnSuccess(typedTuples -> redisRankItems.getAndUpdate(rankItems -> Collections.unmodifiableList(typedTuples))).subscribe();
        }
    }

    private void putMemoryRank(Collection<RankItem> rankItems) {
        if (CollectionUtil.isEmpty(rankItems)) {
            return;
        }
        rankInfoList.removeAll(rankItems);
        rankInfoList.addAll(rankItems);
        rankInfoList.sort(Comparator.reverseOrder());
    }

    private void putRedisRank(List<RankItem> rankItems, boolean alive) {
        if (CollectionUtil.isEmpty(rankItems)) {
            return;
        }
        List<List<RankItem>> partitions = ListUtils.partition(rankItems, 1000);
        for (List<RankItem> partition : partitions) {
            List<ZSetOperations.TypedTuple<String>> rankTuples = partition.stream().map(codec::encode).collect(Collectors.toList());
            Map<String, String> rankExtraMap = partition.stream().filter(e -> e.getRawExtraData() != null).collect(Collectors.toMap(e -> Long.toString(e.getUuid()), e -> JsonUtil.toJson(e.getRawExtraData())));
            if (alive) {
                rankCache.redisTemplate.opsForZSet().addAll(rankKey, rankTuples).subscribe();
                if (MapUtil.isNotEmpty(rankExtraMap)) {
                    rankCache.redisTemplate.opsForHash().putAll(rankExtraKey, rankExtraMap).subscribe();
                }
            } else {
                rankCache.redisTemplate.opsForZSet().addAll(rankKey, rankTuples).block();
                if (MapUtil.isNotEmpty(rankExtraMap)) {
                    rankCache.redisTemplate.opsForHash().putAll(rankExtraKey, rankExtraMap).block();
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
            try {
                flushSemaphore.acquire();
                int size = changedRankItemMap.size();
                List<RankItem> changedRankItems = Lists.newArrayListWithExpectedSize(size);
                for (int i = 0; i < size; i++) {
                    Map.Entry<Long, RankItem> rankItemEntry = changedRankItemMap.pollFirstEntry();
                    if (rankItemEntry == null || rankItemEntry.getValue() == null) break;
                    changedRankItems.add(rankItemEntry.getValue());
                }
                putMemoryRank(changedRankItems);
                putRedisRank(changedRankItems, alive);
                if (rankDeleting) {
                    rankCache.redisTemplate.delete(rankKey).block();
                    rankCache.redisTemplate.delete(rankExtraKey).block();
                    changedRankItemMap.clear();
                    changedRankItemSet.clear();
                    rankInfoList.clear();
                    rankDeleting = false;
                }
                for (Long uuid : rankInfoDeleting) {
                    rankCache.redisTemplate.opsForZSet().remove(rankKey, String.valueOf(uuid)).block();
                    rankCache.redisTemplate.opsForZSet().remove(rankExtraKey, String.valueOf(uuid)).block();
                    rankInfoList.removeIf(rankItem -> rankItem.getUuid() == uuid);
                    changedRankItemMap.remove(uuid);
                    changedRankItemSet.remove(uuid);
                    rankInfoDeleting.remove(uuid);
                }
                if (alive) {
                    loadRedisRankItems(false);
                    truncate();
                } else {
                    putRedisRank(rankInfoList.stream().filter(rankItem -> changedRankItemSet.contains(rankItem.getUuid())).collect(Collectors.toList()), alive);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void truncate() {
        truncateMemoryRank();
        if (truncateSignal.signal()) {
            rankCache.executor.execute(this::truncateRedisRank);
        }
    }

    private void truncateMemoryRank() {
        long oversize = rankInfoList.size() - maxSize;
        for (long i = 0; i < oversize; i++) {
            try {
                rankInfoList.remove(rankInfoList.size() - 1);
            } catch (IndexOutOfBoundsException ex) {
                break;
            }
        }
    }

    private void truncateRedisRank() {
        List<Object> keys = rankCache.redisTemplate.opsForHash().keys(rankExtraKey).collectList().block();
        if (CollectionUtil.isEmpty(keys)) {
            rankCache.redisTemplate.opsForZSet().removeRange(rankKey, Range.closed(0L, -maxSize - 1L)).subscribe();
            return;
        }
        List<String> ranks = rankCache.redisTemplate.opsForZSet().reverseRange(rankKey, Range.closed(0L, maxSize)).collectList().block();
        if (CollectionUtil.isEmpty(ranks)) {
            rankCache.redisTemplate.delete(rankExtraKey).subscribe();
            return;
        }
        Set<String> removeExtraIds = new HashSet<>();
        Set<String> extraIds = keys.stream().map(String::valueOf).collect(Collectors.toSet());
        Set<String> rankIds = ranks.stream().map(String::valueOf).collect(Collectors.toSet());
        for (String extraId : extraIds) {
            if (!rankIds.contains(extraId)) {
                removeExtraIds.add(extraId);
            }
        }
        if (!removeExtraIds.isEmpty()) {
            rankCache.redisTemplate.opsForHash().remove(rankExtraKey, removeExtraIds.toArray()).subscribe();
        }
        //尾删法,防止排行榜末尾变化频繁是，将波动的排行榜额外数据删除掉,A->B->A问题
        //例如：1->查extra=[a,b,c],2->查rank = [a,b,d],3->删除extra=[c],但此时rank又变回[a,b,c],错误的将c删除
        //尾删法,每次删除周期运作时，当前周期内上过榜的元素不删除，仅删除本周期内尾活动且未上榜的元素，为彻底解决，仍有小概率会出现。
        //所以采用，每次定福时,再次保存仍在排行榜上的,本地变化过的热数据,防止错误的删除数据后，如不再触发更新排行榜，会找不到额外数据
        rankCache.redisTemplate.opsForZSet().removeRange(rankKey, Range.closed(0L, -maxSize - 1L)).subscribe();
    }
}
