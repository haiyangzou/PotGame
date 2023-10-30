package org.pot.dal.redis;

import org.pot.common.config.RedisConfig;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

public class ReactiveRedis {
    private static volatile ReactiveStringRedisTemplate local;
    private static volatile ReactiveStringRedisTemplate global;
    private static volatile ReactiveStringRedisTemplate rank;

    public static void init(RedisConfig localConfig, RedisConfig globalConfig, RedisConfig rankConfig) {
        if (localConfig != null) {
            local = createReactiveStringRedisTemplate(localConfig);
        }
    }

    public static ReactiveStringRedisTemplate rank() {
        return rank;
    }

    public static ReactiveStringRedisTemplate createReactiveStringRedisTemplate(RedisConfig redisConfig) {
        RedisConfiguration redisConfiguration = RedisConfiguration.createRedisConfiguration(redisConfig);
        return null;
    }
}
