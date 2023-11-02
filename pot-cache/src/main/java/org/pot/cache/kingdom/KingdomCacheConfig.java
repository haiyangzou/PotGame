package org.pot.cache.kingdom;

import lombok.Data;
import org.apache.commons.configuration2.Configuration;


@Data
public class KingdomCacheConfig {
    private int threads = 1;
    private int maxSize = 10000;
    private long refreshSeconds = 10;

    public static KingdomCacheConfig loadCacheConfig(Configuration config) {
        KingdomCacheConfig cacheConfig = new KingdomCacheConfig();
        if (config == null) {
            return cacheConfig;
        } else {
            cacheConfig.setProperties(config);
        }
        return cacheConfig;
    }

    private void setProperties(Configuration config) {
        this.threads = config.getInt("kingdom.cache.threads", threads);

    }
}
