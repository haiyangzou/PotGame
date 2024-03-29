package org.pot.cache.union;

import lombok.Data;

@Data
public class UnionCacheConfig {
    private static final int MEMBER_AMOUNT = 50;
    private int threads = 3;
    private int maxSize = 500;
    private long refreshSeconds = 60;
    private long expireSeconds = 86400;
    private long flushSecons = 30;
    private int maxMemberSize = maxSize * MEMBER_AMOUNT;

}
