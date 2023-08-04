package org.pot.dal.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.CollectionUtil;
import org.pot.dal.redis.RedisSupport;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisLockFactory {
    private static final long timeout = TimeUnit.MILLISECONDS.toMillis(10);
    private static final Map<String, RedisLock> locks = new ConcurrentHashMap<>();

    static {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    long now = System.currentTimeMillis();
                    ArrayList<String> removes = new ArrayList<>();
                    for (Map.Entry<String, RedisLock> entry : locks.entrySet()) {
                        String name = entry.getKey();
                        RedisLock lock = entry.getValue();
                        if (lock == null || now - lock.timestamp > timeout) {
                            removes.add(name);
                        }
                    }
                    if (CollectionUtil.isNotEmpty(removes)) {
                        int size = 0;
                        for (String remove : removes) {
                            size = delRedisLock(remove, now) ? size + 1 : size;
                        }
                    }
                } catch (Throwable e) {
                    log.error("evict redis lock error", e);
                }
            }
        });
        thread.setName(RedisLockFactory.class.getSimpleName());
        thread.setDaemon(true);
        thread.start();
    }

    private static synchronized boolean delRedisLock(String name, long now) {
        RedisLock lock = locks.get(name);
        if (lock == null || now - lock.timestamp > timeout) {
            return locks.remove(name) != null;
        }
        return false;
    }

    public static synchronized RedisLock getRedisLock(String name, RedisSupport.RedisSession redisSession) {
        return locks.computeIfAbsent(name, k -> new LettuceRedisLock(name, redisSession)).activate();
    }

    public static synchronized RedisLock getRedisLock(String name, ReactiveStringRedisTemplate redisTemplate) {
        return locks.computeIfAbsent(name, k -> new SpringRedisLock(name, redisTemplate)).activate();
    }
}
