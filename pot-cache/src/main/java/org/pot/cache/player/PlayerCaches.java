package org.pot.cache.player;

import lombok.Getter;
import org.pot.cache.player.name.PlayerNameCache;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.cache.player.snapshot.PlayerSnapShotCache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.function.Function;

@Getter
public class PlayerCaches {
    private static PlayerNameCache playerNameCache;
    private static PlayerSnapShotCache playerSnapShot;

    public static void init(PlayerCacheConfig cacheConfig,
                            ReactiveStringRedisTemplate redisTemplate,
                            Function<Long, PlayerSnapShot> memory,
                            Function<Long, PlayerSnapShot> database) {

        PlayerCaches.playerNameCache = new PlayerNameCache(redisTemplate);
        // Initialize a new instance of PlayerSnapshotCache
        PlayerCaches.playerSnapShot = new PlayerSnapShotCache(cacheConfig, redisTemplate, memory, database);
        // Initialize a new instance of PlayerOnlineCountCache
    }

    public static PlayerNameCache name() {
        return playerNameCache;
    }

    public static PlayerSnapShotCache snapShot() {
        return playerSnapShot;
    }

    public synchronized static void shutdown() {
        if (playerSnapShot != null) {
            playerSnapShot.close();
            playerSnapShot = null;
        }
    }
}
