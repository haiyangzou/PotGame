package org.pot.cache.player;

import lombok.Data;
import org.apache.commons.configuration2.Configuration;

@Data
public class PlayerCacheConfig {
    private int threads = 3;
    private int maxSize = 30000;
    private long refreshSeconds = 60;
    private long expireSeconds = 86400;
    private long flushSeconds = 30;

    public static PlayerCacheConfig loadCacheConfig(Configuration config) {
        PlayerCacheConfig cacheConfig = new PlayerCacheConfig();
        return cacheConfig;
    }
}
