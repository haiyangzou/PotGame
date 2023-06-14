package org.pot.cache.player.name;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate; // Import the ReactiveStringRedisTemplate class from the Spring Data Redis library. This class provides a reactive Redis template for Redis key-value operations using Strings. 
import org.springframework.data.redis.core.ReactiveValueOperations;

import io.netty.util.internal.ThreadLocalRandom;

public class PlayerNameCache {
    public static final String CACHE_REDIS_PREFIX = "PlayerNameId";
    private final ReactiveStringRedisTemplate redisTemplate;

    // Class variables and constructor
    public PlayerNameCache(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Method to convert decimal to other base
    public String decimalToOtherBase(long decimal, int base) {
        if (base < 2 || base > 36) {
            throw new IllegalArgumentException("Invalid base: " + base);
        }

        StringBuilder result = new StringBuilder();
        while (decimal > 0) {
            int remainder = (int) (decimal % base);
            char digit = (remainder < 10) ? (char) (remainder + '0') : (char) (remainder - 10 + 'A');
            result.insert(0, digit);
            decimal /= base;
        }

        return result.toString();
    }

    public String newUniqueName(long uid) {
        final int length = 14;
        final String delimiter = "-";
        StringBuilder nameBuilder = new StringBuilder("Lord" + decimalToOtherBase(uid, 62));
        if (nameBuilder.length() < length) {
            nameBuilder.append(delimiter);
        }
        while (nameBuilder.length() < length) {
            nameBuilder.append("a");
        }
        String name = nameBuilder.toString();
        if (putIfAbsent(name, uid)) {
            return name;
        }
        name = name + delimiter;
        while (true) {
            String n = name + decimalToOtherBase(ThreadLocalRandom.current().nextInt(), 62);
            if (putIfAbsent(n, uid)) {
                return n;
            }
        }
    }

    public boolean putIfAbsent(String name, long uid) {
        ReactiveValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = CACHE_REDIS_PREFIX + name;
        Boolean result = valueOperations.setIfAbsent(key, String.valueOf(uid)).block();
        return result == null ? false : result;
    }

    public void remove(String name) {
        ReactiveValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = CACHE_REDIS_PREFIX + name;
        valueOperations.delete(key).subscribe();
    }

    public long getUid(String name) {
        ReactiveValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = CACHE_REDIS_PREFIX + name;
        String uidString = valueOperations.get(key).block();
        return uidString == null ? -1 : Long.parseLong(uidString);
    }
}