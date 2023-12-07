package org.pot.game.util;

import org.pot.cache.player.PlayerCaches;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.game.engine.enums.StatisticsEnum;
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
        PlayerSnapShot playerSnapshot = new PlayerSnapShot();
        playerSnapshot.setUid(player.getUid());
        playerSnapshot.setLevel(player.commonAgent.getLevel());
        playerSnapshot.setName(player.getName());
        playerSnapshot.setServerId(player.getServerId());
        playerSnapshot.setLanguage(player.getProfile().getLanguage());
        playerSnapshot.setAvatar(player.commonAgent.getPlayerCommonInfo().getPicture());
        playerSnapshot.setFrameId(player.commonAgent.getPlayerCommonInfo().getFrameId());
        playerSnapshot.setIcon(player.commonAgent.getPlayerCommonInfo().getIconId());
        playerSnapshot.setUnionId(0);
        playerSnapshot.setUnionName("");
        playerSnapshot.setUnionAlias("");
        playerSnapshot.setUnionRank(-1);
        playerSnapshot.setPower(player.powerComponent.getPower());
        playerSnapshot.setKillCount(player.statisticsComponent.getStatisticsLong(StatisticsEnum.KILL_SOLDIER));
        playerSnapshot.setVarMap(StatisticsEnum.needSendClient(player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().getVarMap()));
        return playerSnapshot;
    }
}
