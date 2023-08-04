package org.pot.dal.redis.lock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.pot.dal.redis.RedisScripts;
import org.springframework.data.redis.connection.ReactiveScriptingCommands;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisElementWriter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SpringRedisLock extends RedisLock {
    private final ReactiveStringRedisTemplate redisTemplate;

    public SpringRedisLock(String name, ReactiveStringRedisTemplate redisTemplate) {
        super(name);
        this.redisTemplate = redisTemplate;
        this.initScripts();
    }

    private void initScripts() {
        ReactiveScriptingCommands commands = redisTemplate.getConnectionFactory().getReactiveConnection().scriptingCommands();
        for (Scripts value : Scripts.values()) {
            RedisScript<Object> redisScript = RedisScripts.of(value.getName());
            if (redisScript == null) {
                throw new IllegalArgumentException("Lock Scripts Not Found");
            }
            Boolean exists = commands.scriptExists(redisScript.getSha1()).block();
            if (BooleanUtils.isNotTrue(exists)) {
                RedisElementWriter<String> writer = redisTemplate.getSerializationContext().getStringSerializationPair().getWriter();
                commands.scriptLoad(writer.write(redisScript.getScriptAsString())).block();
            }
        }
    }

    @Override
    public void lock(long timeout, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        synchronized (name) {
            timeout = System.currentTimeMillis();
            List<String> keys = ImmutableList.of(name, LOCK_ID_GENERATOR);
            List<String> argv = ImmutableList.of(Long.toString(sign.get()), Long.toString(LOCK_EXPIRED_SECONDS));
            RedisScript<String> redisScript = RedisScripts.of(Scripts.LOCK.getName());
            long timeoutMillis = timeout <= 0 ? 0 : timeUnit.toMillis(timeout);
            Stopwatch stopwatch = Stopwatch.createStarted();
            do {
                if (acquire(redisTemplate.execute(redisScript, keys, argv).blockFirst())) {
                    long elapsed = stopwatch.elapsed().toMillis();
                    if (elapsed > SLOW_LOCK_MILLIS) {
                        log.warn("Redis Lock Slow. name = {}", name);
                        RedisLock.elapsedTimeMonitor.recordElapsedTime(name, elapsed);
                    }
                    return;
                }
                TimeUnit.MILLISECONDS.sleep(SPIN_INTERVAL_MILLIS);
            } while (stopwatch.elapsed().toMillis() < timeoutMillis);
            throw new TimeoutException("Redis Lock Timeout.name=" + name);
        }
    }

    @Override
    public void unlock() {
        List<String> keys = ImmutableList.of(name);
        List<String> argv = ImmutableList.of(Long.toString(sign.get()));
        RedisScript<String> redisScript = RedisScripts.of(Scripts.UNLOCK.getName());
        release(redisTemplate.execute(redisScript, keys, argv).blockFirst());
    }
}
