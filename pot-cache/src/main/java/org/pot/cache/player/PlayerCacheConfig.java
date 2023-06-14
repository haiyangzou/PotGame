package org.pot.cache.player;

import lombok.Data;

@Data
public class PlayerCacheConfig {
    private int threads = 3;
    private int maxSize = 30000;
    private long refreshSeconds = 60;
    private long expireSeconds = 86400;
    private long flushSecons = 30;
    
}
