package org.pot.dal.redis;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.api.StatefulConnection;
import org.pot.common.config.RedisConfig;

public class RedisSupport {
    private static volatile RedisSession local;
    private static volatile RedisSession global;

    public static void init(RedisConfig localConfig, RedisConfig globalConfig) {
        if (localConfig != null) {
            RedisSupport.local = new RedisSession(localConfig);
        }
        if (globalConfig != null) {
            RedisSupport.local = new RedisSession(globalConfig);
        }
    }

    public static void shutDown() {
        if (local != null) {
            local.shutdown();
        }
        if (global != null) {
            global.shutdown();
        }
    }

    public static class RedisSession {
        protected AbstractRedisClient client;
        protected StatefulConnection<String, String> connection;

        public RedisSession(RedisConfig redisConfig) {

        }

        public void shutdown() {

        }
    }
}
