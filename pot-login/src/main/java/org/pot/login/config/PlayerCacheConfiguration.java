package org.pot.login.config;

import lombok.Getter;
import lombok.Setter;
import org.pot.cache.player.PlayerCacheConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class PlayerCacheConfiguration {
    @Value("${player.cache.snapshot.threads:3}")
    private int threads;
    @Value("${player.cache.snapshot.max.size:10000}")
    private int maxSize;
    @Value("${player.cache.snapshot.flush.seconds:60}")
    private int flushSeconds;
    @Value("${player.cache.snapshot.expire.seconds:3600}")
    private int expireSeconds;

    public PlayerCacheConfig toPlayerCacheConfig() {
        PlayerCacheConfig playerCacheConfig = PlayerCacheConfig.loadCacheConfig(null);
        return playerCacheConfig;
    }
}
