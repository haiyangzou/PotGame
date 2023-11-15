package org.pot.dal.redis;


import org.pot.dal.RedisUtils;

import java.util.Objects;

public enum RedisKey {
    union("union_snapshot"),
    union_tech("union_tech_snapshot"),
    union_trials("union_trials_snapshot"),
    union_member("union_member_snapshot"),
    union_member_ids("union_member_ids_snapshot"),
    union_activity_data("union_activity_data_snapshot"),
    ;
    private final String prefix;

    RedisKey(String prefix) {
        this.prefix = Objects.requireNonNull(prefix);
    }

    public String build(Object... args) {
        return RedisUtils.buildRedisKey(this.prefix, args);
    }

    public String pattern() {
        return this.prefix + "*";
    }
}
