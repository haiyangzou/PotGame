package org.pot.cache.rank;

import lombok.Data;

@Data
public class RankCacheConfig {
    private int threads = 10;
    private int loadSeconds = 5 * 60;
    private int truncateSeconds = 600;
}
