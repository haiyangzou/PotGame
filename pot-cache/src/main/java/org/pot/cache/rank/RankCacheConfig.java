package org.pot.cache.rank;

import lombok.Data;
import org.apache.commons.configuration2.Configuration;

@Data
public class RankCacheConfig {
    private int threads = 10;
    private int loadSeconds = 5 * 60;
    private int truncateSeconds = 600;

    public static RankCacheConfig loadConfig(Configuration config) {
        RankCacheConfig rankCacheConfig = new RankCacheConfig();
        if (config == null) {
            return rankCacheConfig;
        } else {
            rankCacheConfig.setProperties(config);
        }
        return rankCacheConfig;
    }

    private void setProperties(Configuration config) {

    }
}
