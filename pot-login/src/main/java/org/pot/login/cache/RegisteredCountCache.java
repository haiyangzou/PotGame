package org.pot.login.cache;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.communication.server.GameServer;
import org.pot.common.date.DateTimeString;
import org.pot.common.util.CollectionUtil;
import org.pot.core.util.RedisUtils;
import org.pot.login.entity.RegisterGroupLocale;
import org.pot.login.entity.RegisterPolicy;
import org.pot.login.entity.RegisterPolicyGroup;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RegisteredCountCache {
    private static final String KEY_PREFIX = RedisUtils.buildRedisKey("GLOBAL", "REGISTER", "COUNT");
    private final Map<String, Long> registeredCountMap = new ConcurrentHashMap<>();
    @Resource
    private ReactiveStringRedisTemplate globalReactiveRedisTemplate;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<String> keys = Lists.newArrayList(registeredCountMap.keySet());
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }
        List<String> values = globalReactiveRedisTemplate.opsForValue().multiGet(keys).block();
        if (CollectionUtil.isEmpty(values)) {
            return;
        }
        Map<String, Long> temp = new HashMap<>();
        for (int i = 0; i < keys.size() && i < values.size(); i++) {
            String key = keys.get(i);
            String value = values.get(i);
            temp.put(key, NumberUtils.toLong(value, -1));
        }
        registeredCountMap.putAll(temp);
        registeredCountMap.keySet().retainAll(temp.keySet());
    }

    private String getPolicyTotalKey(RegisterPolicy policy) {
        return RedisUtils.buildRedisKey(KEY_PREFIX, "POLICY", policy.getId(), "TOTAL");
    }

    private String getPolicyDayKey(RegisterPolicy policy) {
        LocalDateTime now = LocalDateTime.now();
        return RedisUtils.buildRedisKey(KEY_PREFIX, "POLICY", policy.getId(), "DAY", DateTimeString.DATE.toString(now));
    }

    private String getPolicyHourKey(RegisterPolicy policy) {
        LocalDateTime now = LocalDateTime.now();
        return RedisUtils.buildRedisKey(KEY_PREFIX, "POLICY", policy.getId(), "DAY", DateTimeString.DATE.toString(now), "HOUR", now.getHour());
    }

    private String getServerTotalKey(GameServer gameServer) {
        return RedisUtils.buildRedisKey(KEY_PREFIX, "POLICY", gameServer.getServerId(), "TOTAL");
    }

    private String getServerDayKey(GameServer gameServer) {
        LocalDateTime now = LocalDateTime.now();
        return RedisUtils.buildRedisKey(KEY_PREFIX, "SERVER", gameServer.getServerId(), "DAY", DateTimeString.DATE.toString(now));
    }

    private String getServerHourKey(GameServer gameServer) {
        LocalDateTime now = LocalDateTime.now();
        return RedisUtils.buildRedisKey(KEY_PREFIX, "SERVER", gameServer.getServerId(), "DAY", DateTimeString.DATE.toString(now), "HOUR", now.getHour());
    }

    private String getServerPolicyGroupKey(GameServer gameServer, RegisterPolicyGroup policyGroup) {
        return RedisUtils.buildRedisKey(KEY_PREFIX, "POLICY", policyGroup.getPolicyId(), "GROUP", policyGroup.getGroupId());
    }

    private String getServerPolicyGroupLocaleKey(GameServer gameServer, RegisterPolicyGroup policyGroup, RegisterGroupLocale groupLocale) {
        return RedisUtils.buildRedisKey(KEY_PREFIX, "POLICY", policyGroup.getPolicyId(), "GROUP", policyGroup.getGroupId(), "LOCALE", groupLocale.getLocaleId());
    }

    public boolean isNotFullPolicy(RegisterPolicy policy) {
        int totalCount = getInt(getPolicyTotalKey(policy));
        int dayCount = getInt(getPolicyDayKey(policy));
        int hourCount = getInt(getPolicyHourKey(policy));
        return (policy.getTotalMaxCount() <= 0 || totalCount < policy.getTotalMaxCount())
                && (policy.getDayMaxCount() <= 0 || dayCount < policy.getDayMaxCount())
                && (policy.getHourMaxCount() <= 0 || hourCount < policy.getHourMaxCount());
    }

    public int getInt(String key) {
        Long count = registeredCountMap.get(key);
        if (count == null) {
            String value = globalReactiveRedisTemplate.opsForValue().get(key).block();
            count = registeredCountMap.computeIfAbsent(key, k -> NumberUtils.toLong(value, -1));
        }
        return count.intValue();
    }

    public boolean isNotFullServer(GameServer gameServer) {
        int totalCount = getInt(getServerTotalKey(gameServer));
        int dayCount = getInt(getServerDayKey(gameServer));
        int hourCount = getInt(getServerHourKey(gameServer));
        return (gameServer.getTotalMaxCount() <= 0 || totalCount < gameServer.getTotalMaxCount())
                && (gameServer.getDayMaxCount() <= 0 || dayCount < gameServer.getDayMaxCount())
                && (gameServer.getHourMaxCount() <= 0 || hourCount < gameServer.getHourMaxCount());

    }

    public int getServerPolicyGroupCount(GameServer policyServer, RegisterPolicyGroup policyGroup) {
        return getInt(getServerPolicyGroupKey(policyServer, policyGroup));
    }

    public void addServerPolicyGroupCount(GameServer policyServer, RegisterPolicyGroup policyGroup) {
        String key = getServerPolicyGroupKey(policyServer, policyGroup);
        globalReactiveRedisTemplate.opsForValue().increment(key).subscribe(count -> registeredCountMap.put(key, count));
    }

    public int getServerPolicyGroupLocaleCount(GameServer policyServer, RegisterPolicyGroup policyGroup, RegisterGroupLocale groupLocale) {
        return getInt(getServerPolicyGroupLocaleKey(policyServer, policyGroup, groupLocale));
    }

    public void addServerPolicyGroupLocaleCount(GameServer policyServer, RegisterPolicyGroup policyGroup, RegisterGroupLocale groupLocale) {
        String key = getServerPolicyGroupLocaleKey(policyServer, policyGroup, groupLocale);
        globalReactiveRedisTemplate.opsForValue().increment(key).subscribe(count -> registeredCountMap.put(key, count));
    }

    public void addPolicyCount(RegisterPolicy registerPolicy) {
        String totalKey = getPolicyTotalKey(registerPolicy);
        globalReactiveRedisTemplate.opsForValue().increment(totalKey).subscribe(count -> registeredCountMap.put(totalKey, count));
        String dayKey = getPolicyDayKey(registerPolicy);
        globalReactiveRedisTemplate.opsForValue().increment(dayKey).subscribe(count -> registeredCountMap.put(totalKey, count));
        globalReactiveRedisTemplate.expire(dayKey, Duration.ofDays(2)).subscribe();
        String hourKey = getPolicyHourKey(registerPolicy);
        globalReactiveRedisTemplate.opsForValue().increment(hourKey).subscribe(count -> registeredCountMap.put(totalKey, count));
        globalReactiveRedisTemplate.expire(hourKey, Duration.ofHours(2)).subscribe();
    }

    public void addServerCount(GameServer policyServer) {
        String totalKey = getServerTotalKey(policyServer);
        globalReactiveRedisTemplate.opsForValue().increment(totalKey).subscribe(count -> registeredCountMap.put(totalKey, count));
        String dayKey = getServerDayKey(policyServer);
        globalReactiveRedisTemplate.opsForValue().increment(dayKey).subscribe(count -> registeredCountMap.put(totalKey, count));
        globalReactiveRedisTemplate.expire(dayKey, Duration.ofDays(2)).subscribe();
        String hourKey = getServerHourKey(policyServer);
        globalReactiveRedisTemplate.opsForValue().increment(hourKey).subscribe(count -> registeredCountMap.put(totalKey, count));
        globalReactiveRedisTemplate.expire(hourKey, Duration.ofHours(2)).subscribe();
    }

}
