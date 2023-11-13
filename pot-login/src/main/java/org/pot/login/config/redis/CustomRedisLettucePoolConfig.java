package org.pot.login.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

@Slf4j
public class CustomRedisLettucePoolConfig<T> extends GenericObjectPoolConfig<T> {
    @Value("${global.redis.lettuce.pool.min-idle:20}")
    private int minIdle;
    @Value("${global.redis.lettuce.pool.max-idle:20}")
    private int maxIdle;
    @Value("${global.redis.lettuce.pool.max-total:100}")
    private int maxTotal;
    @Value("${global.redis.lettuce.pool.test-on-borrow:false}")
    private boolean testOnBorrow;
    @Value("${global.redis.lettuce.pool.test-on-return:false}")
    private boolean testOnReturn;
    @Value("${global.redis.lettuce.pool.test-on-create:false}")
    private boolean testOnCreate;
    @Value("${global.redis.lettuce.pool.test-on-while-idle:false}")
    private boolean testWhileIdle;
    @Value("${global.redis.lettuce.pool.block-when-exhausted:false}")
    private boolean blockWhenExhausted;
    @Value("${global.redis.lettuce.pool.max-wait-millis:1000}")
    private long maxWaitMillis;
    @Value("${global.redis.lettuce.pool.time-between-eviction-runs-mills:600000}")
    private long timeBetweenEvictionRunsMills;

    @PostConstruct
    public void postConstruct() throws Exception {
        setMinIdle(minIdle);
        setMaxIdle(maxTotal);
        setMaxTotal(maxTotal);
        setTestOnBorrow(testOnBorrow);
        setTestOnReturn(testOnReturn);
        setTestOnCreate(testOnCreate);
        setTestWhileIdle(testWhileIdle);
        setBlockWhenExhausted(blockWhenExhausted);
        setMaxWaitMillis(maxWaitMillis);
        setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMills);
    }
}
