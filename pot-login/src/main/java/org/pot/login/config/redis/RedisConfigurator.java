package org.pot.login.config.redis;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Configuration
public class RedisConfigurator {
    @Bean
    @ConfigurationProperties(prefix = "global.redis.lettuce.pool")
    public GenericObjectPoolConfig<?> globalRedisLettuceClientPoolConfig() {
        return new CustomRedisLettucePoolConfig<>();
    }

    @Bean
    public RedisConfiguration globalRedisConfiguration(@Value("${global.redis.password:}") String password,
                                                       @Value("${global.redis.database:0}") int database,
                                                       @Value("${global.redis.host:}") String host,
                                                       @Value("${global.redis.port:6379}") int port,
                                                       @Value("${global.redis.nodes:}") String nodes,
                                                       @Value("${global.redis.nodes.maxRedirects:6}") int maxRedirects,
                                                       @Value("${global.redis.nodes.topologyRefreshSeconds:6}") int topologyRefreshSeconds) {
        if (StringUtils.isBlank(nodes)) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                    host, port
            );
            if (StringUtils.isNotBlank(password)) {
                redisStandaloneConfiguration.setPassword(password);
            }
            redisStandaloneConfiguration.setDatabase(database);
            return redisStandaloneConfiguration;
        } else {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(
                    Arrays.asList(StringUtils.split(nodes, ","))
            );
            if (StringUtils.isNotBlank(password)) {
                redisClusterConfiguration.setPassword(password);
            }
            redisClusterConfiguration.setMaxRedirects(maxRedirects);
            return redisClusterConfiguration;
        }
    }

    @Bean
    public LettucePoolingClientConfiguration globalLettucePoolingClientConfiguration(@Autowired RedisConfiguration globalRedisConfiguration,
                                                                                     @Autowired GenericObjectPoolConfig<?> globalRedisLettuceClientPoolConfig,
                                                                                     @Value("${global.redis.nodes.maxRedirects:6}") int maxRedirects,
                                                                                     @Value("${global.redis.nodes.topologyRefreshSeconds:6}") int topologyRefreshSeconds) {
        if (globalRedisConfiguration instanceof RedisStandaloneConfiguration) {
            return LettucePoolingClientConfiguration.builder().poolConfig(globalRedisLettuceClientPoolConfig).build();
        } else {
            ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .enableAllAdaptiveRefreshTriggers()
                    .enablePeriodicRefresh(Duration.ofSeconds(topologyRefreshSeconds))
                    .build();
            ClusterClientOptions clusterCLientOptions = ClusterClientOptions.builder()
                    .autoReconnect(true)
                    .maxRedirects(maxRedirects)
                    .topologyRefreshOptions(clusterTopologyRefreshOptions)
                    .build();
            return LettucePoolingClientConfiguration.builder()
                    .poolConfig(globalRedisLettuceClientPoolConfig)
                    .clientOptions(clusterCLientOptions)
                    .build();
        }
    }

    @Bean
    public ReactiveStringRedisTemplate globalReactiveRedisTemplate(@Autowired RedisConfiguration globalRedisConfiguration,
                                                                   @Autowired LettucePoolingClientConfiguration globalLettucePoolingClientConfiguration) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(globalRedisConfiguration, globalLettucePoolingClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return new ReactiveStringRedisTemplate(lettuceConnectionFactory);
    }
}
