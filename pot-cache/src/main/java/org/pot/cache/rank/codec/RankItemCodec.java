package org.pot.cache.rank.codec;

import org.pot.cache.rank.RankItem;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Nullable;

public interface RankItemCodec {
    @Nullable
    RankItem decode(ZSetOperations.TypedTuple<String> tuple);

    ZSetOperations.TypedTuple<String> encode(RankItem rankItem);

    DefaultRankItemCodec DEFAULT = new DefaultRankItemCodec();

    TimeRankItemCodec TIME = new TimeRankItemCodec();

}
