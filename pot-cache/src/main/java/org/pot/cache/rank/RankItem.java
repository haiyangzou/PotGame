package org.pot.cache.rank;

import lombok.Getter;
import org.pot.cache.rank.extra.RankItemExtraData;
import org.pot.common.util.JsonUtil;

import java.util.Objects;

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
        int result = Long.compare(this.score, o.score);
        if (result == 0) {
            result = Long.compare(o.timestamp, timestamp);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RankItem rankItem = (RankItem) obj;
        return uuid == rankItem.getUuid();
    }

    public RankItem copy() {
        RankItem rankItem = new RankItem(uuid, score, timestamp);
        if (this.extraData != null) {
            rankItem.extraData = JsonUtil.clone(extraData);
        }
        return rankItem;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    public void parseExtraData(Object json) {
        if (json != null) {
            extraData = JsonUtil.parseJson(json.toString(), RankItemExtraData.class);
        }
    }

    public RankItemExtraData getRawExtraData() {
        return extraData;
    }

    public <T extends RankItemExtraData> T getExtraData(Class<T> cls) {
        if (extraData == null) return null;
        Class<? extends RankItemExtraData> extraDataType = extraData.getClass();
        if (extraDataType != null && cls.isAssignableFrom(extraDataType)) {
            return (T) extraData;
        }
        return null;
    }
}
