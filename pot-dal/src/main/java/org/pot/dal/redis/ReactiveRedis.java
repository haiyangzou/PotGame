package org.pot.dal.redis;

import org.pot.common.config.RedisConfig;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

public class ReactiveRedis {
    private static volatile ReactiveStringRedisTemplate local;
    private static volatile ReactiveStringRedisTemplate global;
    private static volatile ReactiveStringRedisTemplate rank;

    public static void init(RedisConfig localConfig, RedisConfig globalConfig, RedisConfig rankConfig) {
        if (localConfig != null) {
            local = createReactiveStringRedisTemplate(localConfig);
        }
        if (globalConfig != null) {
            global = createReactiveStringRedisTemplate(globalConfig);
        }
        if (rankConfig != null) {
            rank = createReactiveStringRedisTemplate(rankConfig);
        }
    }

    public static void close() {
        if (local != null) {
            ((LettuceConnectionFactory) local.getConnectionFactory()).destroy();
            local = null;
        }
        if (global != null) {
            ((LettuceConnectionFactory) global.getConnectionFactory()).destroy();
            global = null;
        }
        if (rank != null) {
            ((LettuceConnectionFactory) rank.getConnectionFactory()).destroy();
            rank = null;
        }
    }

    public static ReactiveStringRedisTemplate global() {
        return global;
    }

    public static ReactiveStringRedisTemplate rank() {
        return rank;
    }

    public static ReactiveStringRedisTemplate createReactiveStringRedisTemplate(RedisConfig redisConfig) {
        RedisConfiguration redisConfiguration = RedisConfigurator.createRedisConfiguration(redisConfig);
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration
                = RedisConfigurator.createLettucePoolingClientConfiguration(redisConfig, redisConfiguration);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettucePoolingClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return new ReactiveStringRedisTemplate(lettuceConnectionFactory);
    }
}
