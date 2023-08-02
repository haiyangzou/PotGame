package org.pot.cache.rank;

import lombok.Getter;
import org.pot.cache.rank.extra.RankItemExtraData;

public class RankItem implements Comparable<RankItem> {
    @Getter
    private long uuid;
    private long score;
    private long timestamp;
    private RankItemExtraData extraData;

    public RankItem(long uuid, long score) {
        this.uuid = uuid;
        this.score = score;
    }

    public RankItem(long uuid, long score, long timestamp) {
        this.uuid = uuid;
        this.score = score;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(RankItem o) {
        return 0;
    }
}
