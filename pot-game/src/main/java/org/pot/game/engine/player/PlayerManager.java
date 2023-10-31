package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.cache.player.PlayerCaches;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.id.UniqueIdUtil;
import org.pot.core.util.NewDay;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.GameEngineConfig;
import org.pot.game.gate.PlayerSession;
import org.pot.game.util.PlayerSnapshotUtil;
import org.pot.message.protocol.login.LoginDataS2S;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PlayerManager {
    @Getter
    private static final PlayerManager instance = new PlayerManager();
    private PlayerGroup[] playerGroups;

    public PlayerManager() {
    }

    public static Player fetchPlayer(long gameUid) {
        return getInstance().getPlayer(gameUid);
    }

    public Player getPlayer(long gameUid) {
        return null;
    }

    public PlayerGroup getPlayerGroup(int index) {
        return playerGroups[index];
    }

    public PlayerGroup getPlayerGroup(long gameUid) {
        return getPlayerGroup(UniqueIdUtil.index(gameUid, playerGroups.length));
    }

    public LoginDataS2S loginPlayer(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        return getPlayerGroup(loginDataS2S.getGameUid()).loginPlayer(playerSession, loginDataS2S);
    }

    public Player buildPlayer(PlayerSession playerSession, PlayerData playerData) {
        return getPlayerGroup(playerData.getUid()).buildPlayer(playerSession, playerData);
    }

    public boolean isPlayerRunning(long gameUid) {
        return getPlayerGroup(gameUid).isPlayerRunning(gameUid);
    }

    public boolean isPlayerExists(long gameUid) {
        return getPlayerGroup(gameUid).isPlayerExists(gameUid);
    }

    public long getPlayerCount() {
        long total = 0;
        for (PlayerGroup group : playerGroups) {
            total += group.getPlayerCount();
        }
        return total;
    }

    public long getLoadingPlayerCount() {
        long total = 0;
        for (PlayerGroup group : playerGroups) {
            total += group.getLoadingPlayerCount();
        }
        return total;
    }

    public void init() {
        GameEngineConfig config = GameEngine.getInstance().getConfig();
        PlayerCaches.init(config.getPlayerCacheConfig(), ReactiveRedis.global(), PlayerSnapshotUtil::getMemoryPlayerSnapshot, null);
        this.playerGroups = new PlayerGroup[config.getPlayerGroupSize()];
        for (int i = 0; i < playerGroups.length; i++) {
            playerGroups[i] = new PlayerGroup(i);
        }
        NewDay.addDayTask(() -> {
            foreachRunningPlayer(Player::onNewDay);
        });
        NewDay.addWeekTask(() -> foreachRunningPlayer(Player::onNewWeek));
        PlayerAsyncTask.init();
    }

    public void foreachRunningPlayer(Consumer<Player> consumer) {
        for (PlayerGroup group : playerGroups) {
            group.foreachRunningPlayer(consumer);
        }
    }

    public void close() {
        PlayerAsyncTask.shutdown();
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> {
            Arrays.stream(playerGroups).forEach(PlayerGroup::close);
            return Arrays.stream(playerGroups).allMatch(PlayerGroup::isClosed);
        });
        PlayerCaches.shutdown();
    }
}
