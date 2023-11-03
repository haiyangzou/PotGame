package org.pot.game.util;

import org.pot.cache.player.PlayerCaches;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;

public class PlayerSnapshotUtil {
    public static PlayerSnapShot getMemoryPlayerSnapshot(long playerId) {
        if (PlayerManager.getInstance().isPlayerRunning(playerId)) {
            Player player = PlayerManager.fetchPlayer(playerId);
            return player == null ? null : toPlayerSnapshot(player);
        }
        return null;
    }

    public static void updateSnapshot(Player player) {
        PlayerSnapShot playerSnapShot = toPlayerSnapshot(player);
        PlayerCaches.snapShot().putSnapshot(playerSnapShot);
    }

    private static PlayerSnapShot toPlayerSnapshot(Player player) {
        PlayerSnapShot playerSnapShot = new PlayerSnapShot();
        playerSnapShot.setAccount("");
        return playerSnapShot;
    }
}
