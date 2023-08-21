package org.pot.cache.union;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

@Getter
public class UnionMemberSnapshot {
    private long playerId;
    private long unionId;

    public UnionMemberSnapshot(Map<String, String> member) {
        this.playerId = NumberUtils.toLong(member.get("playerId"));
        this.unionId = NumberUtils.toLong(member.get("unionId"));
    }
}
