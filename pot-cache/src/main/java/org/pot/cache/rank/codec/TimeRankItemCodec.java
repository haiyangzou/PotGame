package org.pot.cache.rank.codec;

import org.pot.cache.rank.RankItem;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Nullable;

public class TimeRankItemCodec implements RankItemCodec{
    @Nullable
    @Override
    public RankItem decode(ZSetOperations.TypedTuple<String> tuple) {
        return null;
    }

    @Override
    public ZSetOperations.TypedTuple<String> encode(RankItem rankItem) {
        return null;
    }
}
