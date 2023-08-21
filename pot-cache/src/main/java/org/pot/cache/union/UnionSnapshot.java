package org.pot.cache.union;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.Set;

@Getter
public class UnionSnapshot {
    private long unionId;
    private String name;
    private String alias;
    private Set<Long> members;
    private Map<String, String> unionTeachDataMap;

    public UnionSnapshot(Map<String, String> union, Set<Long> members, Map<String, String> unionTeachDataMap) {
        this.unionId = NumberUtils.toLong(union.get("unionId"));
        this.name = union.get("name");
        this.alias = union.get("alias");
        this.members = members;
        this.unionTeachDataMap = unionTeachDataMap;
    }

    public boolean isMember(long playerId) {
        return members.contains(playerId);
    }
}
