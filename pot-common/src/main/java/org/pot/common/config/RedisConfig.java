package org.pot.common.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

@Slf4j
@Getter
public class RedisConfig {

    String nodes;
    private String password;
    private int database = 0;
    private String host;
    private int port = 6379;
    private int minIdle = 20;
    private int maxIdle = 20;
    private int maxTotal = 100;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean testOnCreate = false;
    private boolean testWhileIdle = false;
    private boolean testWhenExhausted = false;
    private long maxWaitMillis = 1000;
    private long timeBetweenEvictionRunsMills = 600000;
    private int maxRedirects = 6;
    private int topologyRefreshSeconds = 6;

    public RedisConfig(String nodes, String host) {
        this.nodes = nodes;
        this.host = host;
    }

    public boolean isStandalone() {
        return StringUtils.isBlank(nodes);
    }

    public GenericObjectPoolConfig<?> createPoolConfig() {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        poolConfig.setTestOnCreate(testOnCreate);
        poolConfig.setTestWhileIdle(testWhileIdle);
        poolConfig.setBlockWhenExhausted(testWhenExhausted);
        poolConfig.setMaxWait(Duration.ofMillis(maxWaitMillis));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(timeBetweenEvictionRunsMills));
        return poolConfig;
    }
}
