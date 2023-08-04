package org.pot.dal.redis.lock;

import org.pot.dal.redis.RedisSupport;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LettuceRedisLock extends RedisLock {

    protected LettuceRedisLock(String name, RedisSupport.RedisSession redisSession) {
        super(name);
    }

    @Override
    public void lock(long timeout, TimeUnit timeUnit) throws TimeoutException, InterruptedException {

    }

    @Override
    public void unlock() {

    }
}
