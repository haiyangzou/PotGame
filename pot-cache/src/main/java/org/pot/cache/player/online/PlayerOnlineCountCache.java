package org.pot.cache.player.online;

import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.date.DateTimeString;
import org.pot.dal.RedisUtils;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.time.LocalDateTime;

public class PlayerOnlineCountCache {
    private static final String COUNT_KEY_PREFIX = RedisUtils.buildRedisKey("GAME", "PLAYER", "ONLINE", "COUNT");
    private final ReactiveStringRedisTemplate redisTemplate;

    public PlayerOnlineCountCache(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getServerTotalKey(int serverId) {
        return RedisUtils.buildRedisKey(COUNT_KEY_PREFIX, "SERVER", serverId, "TOTAL");
    }

    private String getServerDayKey(int serverId) {
        return getServerDayKey(serverId, LocalDateTime.now());
    }

    private String getServerDayKey(int serverId, LocalDateTime localDateTime) {
        return RedisUtils.buildRedisKey(COUNT_KEY_PREFIX, "SERVER", serverId, "DAY", DateTimeString.DATE.toString(localDateTime));
    }

    private String getServerHourKey(int serverId) {
        return getServerHourKey(serverId, LocalDateTime.now());
    }

    private String getServerHourKey(int serverId, LocalDateTime localDateTime) {
        return RedisUtils.buildRedisKey(COUNT_KEY_PREFIX, "SERVER", serverId, "HOUR", DateTimeString.DATE.toString(localDateTime), "HOUR", localDateTime.getHour());
    }

    public void setServerTotalOnlineCount(int serverId, long count) {
        String totalKey = getServerTotalKey(serverId);
        redisTemplate.opsForValue().set(totalKey, String.valueOf(count)).subscribe();
    }

    public int getServerTotalOnlineCount(int serverId) {
        String totalKey = getServerTotalKey(serverId);
        String count = redisTemplate.opsForValue().get(totalKey).block();
        return NumberUtils.toInt(count, 0);
    }

    public void setServerHourOnlineCount(int serverId, long count) {
        String hourKey = getServerHourKey(serverId);
        redisTemplate.opsForValue().set(hourKey, String.valueOf(count)).subscribe();
    }

    public int getServerHourOnlineCount(int serverId) {
        return getServerHourOnlineCount(serverId, LocalDateTime.now());
    }

    public int getServerHourOnlineCount(int serverId, LocalDateTime localDateTime) {
        String hourKey = getServerHourKey(serverId, localDateTime);
        String count = redisTemplate.opsForValue().get(hourKey).block();
        return NumberUtils.toInt(count, 0);
    }
}
