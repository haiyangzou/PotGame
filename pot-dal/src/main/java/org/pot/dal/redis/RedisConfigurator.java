package org.pot.dal.redis;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.config.RedisConfig;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.Arrays;

public final class RedisConfigurator {
    static RedisConfiguration createRedisConfiguration(RedisConfig redisConfig) {
        if (redisConfig.isStandalone()) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                    redisConfig.getHost(), redisConfig.getPort()
            );
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                redisStandaloneConfiguration.setPassword(redisConfig.getPassword());
            }
            redisStandaloneConfiguration.setDatabase(redisConfig.getDatabase());
            return redisStandaloneConfiguration;
        } else {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(
                    Arrays.asList(StringUtils.split(redisConfig.getNodes(), ","))
            );
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                redisClusterConfiguration.setPassword(redisConfig.getPassword());
            }
            redisClusterConfiguration.setMaxRedirects(redisConfig.getMaxRedirects());
            return redisClusterConfiguration;
        }
    }

    static LettucePoolingClientConfiguration createLettucePoolingClientConfiguration(RedisConfig redisConfig, RedisConfiguration redisConfiguration) {
        if (redisConfiguration instanceof RedisStandaloneConfiguration) {
            return LettucePoolingClientConfiguration.builder().poolConfig(redisConfig.createPoolConfig()).build();
        } else {
            return LettucePoolingClientConfiguration.builder()
                    .poolConfig(redisConfig.createPoolConfig())
                    .clientOptions(createClusterClientOptions(redisConfig))
                    .build();
        }
    }

    static ClusterClientOptions createClusterClientOptions(RedisConfig redisConfig) {
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers()
                .enablePeriodicRefresh(Duration.ofSeconds(redisConfig.getTopologyRefreshSeconds()))
                .build();
        return ClusterClientOptions.builder()
                .autoReconnect(true)
                .maxRedirects(redisConfig.getMaxRedirects())
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .build();
    }
}
