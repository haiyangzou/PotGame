package org.pot.dal.redis.lock;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.structure.ElapsedTimeMonitor;
import org.pot.dal.RedisUtils;
import org.pot.dal.redis.RedisScripts;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public abstract class RedisLock {
    public static ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(RedisLock.class.getName(), "ms");

    enum Scripts implements RedisScripts.RedisScriptDef {
        LOCK("RedisLockScripts/lock.lua"),
        UNLOCK("RedisLockScripts/unlock.lua"),
        ;
        @Getter
        private final String name;
        @Getter
        private final String path;

        Scripts(String path) {
            this.name = path;
            this.path = path;
        }
    }

    static {
        try {
            RedisScripts.load(Scripts.values());
        } catch (Throwable e) {
            throw new RuntimeException("Init Redis Lock Scripts Error");
        }
    }

    @Getter
    protected final String name;
    static final String LOCK_ID_GENERATOR = "{LOCK}_ID_GENERATOR";
    static final long LOCK_EXPIRED_SECONDS = 15;//锁过期时长(秒)
    static final long SPIN_INTERVAL_MILLIS = 10;//自旋锁间隔(秒)
    static final long SLOW_LOCK_MILLIS = 200;//慢速锁时长(秒)
    static final String LOCK_NAME_PREFIX = "{LOCK}_NM";

    protected RedisLock(String name) {
        this.name = RedisUtils.buildRedisKey(LOCK_NAME_PREFIX, name).intern();
    }

    @Getter
    protected volatile long timestamp = System.currentTimeMillis();
    protected final ThreadLocal<Long> sign = ThreadLocal.withInitial(() -> 0L);

    protected RedisLock activate() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    protected boolean acquire(String result) {
        boolean locked;
        int index = result == null ? -1 : result.indexOf(':');
        long lock = index < 0 ? 0 : NumberUtils.toLong(result.substring(0, index));
        long hold = index < 0 ? 0 : NumberUtils.toLong(result.substring(index + 1));
        if (lock <= 0) {
            //没有获取锁
            log.info("Redis Lock Failed. name={},sign={},lock={},hold={}", name, sign.get(), lock, hold);
            sign.set(lock);
            locked = false;
        } else if (lock == sign.get()) {
            //获得重入锁
            log.info("Redis Lock Reentrant. name={},sign={},lock={},hold={}", name, sign.get(), lock, hold);
            sign.set(lock);
            locked = true;
        } else {
            //获得生成锁
            log.info("Redis Lock Generated. name={},sign={},lock={},hold={}", name, sign.get(), lock, hold);
            sign.set(lock);
            locked = true;
        }
        return locked;
    }

    protected void release(String result) {
        int index = result == null ? -1 : result.indexOf(':');
        long lock = index < 0 ? 0 : NumberUtils.toLong(result.substring(0, index));
        long hold = index < 0 ? 0 : NumberUtils.toLong(result.substring(index + 1));
        if (lock <= 0) {
            //没有获取锁
            log.info("Redis UnLock TimeOut. name={},sign={},lock={},hold={}", name, sign.get(), lock, hold);
            sign.set(0L);
        } else if (lock == sign.get()) {
            //获得重入锁
            log.info("Redis UnLock Success. name={},sign={},lock={},hold={}", name, sign.get(), lock, hold);
            if (hold <= 0) sign.set(0L);
        } else {
            //获得生成锁
            log.error("Redis UnLock Timeout Has Benn Locked by Other. name={},sign={},lock={},hold={}", name, sign.get(), lock, hold);
            sign.set(0L);
        }
    }

    public void tryLock() throws IllegalAccessException {
        try {
            lock(0, TimeUnit.SECONDS);
        } catch (Throwable e) {

        }
    }

    public void lock() throws TimeoutException, InterruptedException {
        lock(1, TimeUnit.SECONDS);
    }

    public abstract void lock(long timeout, TimeUnit timeUnit) throws TimeoutException, InterruptedException;

    public abstract void unlock();
}
