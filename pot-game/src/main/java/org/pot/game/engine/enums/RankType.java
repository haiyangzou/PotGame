package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.cache.rank.codec.RankItemCodec;
import org.pot.dal.RedisUtils;
import org.pot.game.engine.GameServerInfo;

@Getter
public enum RankType {
    POWER(1, "RANK_POWER", false),
    ;
    private final int id;
    private final String redisKey;
    private final boolean isUnion;
    private final RankItemCodec codec;

    RankType(int id, String redisKey, boolean isUnion) {
        this(id, redisKey, isUnion, RankItemCodec.DEFAULT);
    }

    RankType(int id, String redisKey, boolean isUnion, RankItemCodec codec) {
        this.id = id;
        this.redisKey = redisKey;
        this.isUnion = isUnion;
        this.codec = codec;
    }

    public String getRankKey(boolean isGlobal) {
        return getRankKey(redisKey, isGlobal);
    }

    public static String getRankKey(String redisKey, boolean isGlobal) {
        return isGlobal ? RedisUtils.buildRedisKey("GLOBAL", redisKey) : RedisUtils.buildRedisKey("LOCAL", redisKey, GameServerInfo.getServerId());
    }

}
