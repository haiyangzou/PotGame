package org.pot.chat.engine;

import org.pot.cache.player.PlayerCacheConfig;
import org.pot.common.config.RedisConfig;
import org.pot.core.engine.EngineConfig;
import org.apache.commons.configuration2.Configuration;

public class ChatEngineConfig extends EngineConfig {

    private volatile RedisConfig chatRedisConfig;

    private volatile PlayerCacheConfig playerCacheConfig;


    public RedisConfig getChatRedisConfig() {
        return this.chatRedisConfig;
    }

    public PlayerCacheConfig getPlayerCacheConfig() {
        return this.playerCacheConfig;
    }

    protected void setProperties(Configuration config) {
        super.setProperties(config);
        this.chatRedisConfig = RedisConfig.loadRedisConfig("chat", config);
        this.playerCacheConfig = PlayerCacheConfig.loadCacheConfig(config);
    }
}