package org.pot.cache.rank.codec;

import org.apache.commons.lang3.math.NumberUtils;
import org.pot.cache.rank.RankItem;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Nullable;

public class DefaultRankItemCodec implements RankItemCodec {
    public DefaultRankItemCodec() {
    }

    @Nullable
    @Override
    public RankItem decode(ZSetOperations.TypedTuple<String> tuple) {
        Double score = tuple.getScore();
        String value = tuple.getValue();
        if (NumberUtils.isParsable(value) && score != null) {
            return parseRedisZSetScore(Long.parseLong(value), score);
        }
        return null;
    }

    @Override
    public ZSetOperations.TypedTuple<String> encode(RankItem rankItem) {
        return null;
    }

    private static final long p = 1000000000L;//100亿

    private RankItem parseRedisZSetScore(long uuid, Double score) {
        if (score < p) {
            return new RankItem(uuid, score.longValue());
        }
        return null;
    }

}
