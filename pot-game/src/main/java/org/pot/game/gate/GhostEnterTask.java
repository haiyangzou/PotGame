package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAsyncTask;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.engine.player.PlayerManager;
import org.pot.message.protocol.tunnel.GhostEnterCmd;
import org.pot.message.protocol.tunnel.GhostEnterErrorCmd;
import org.pot.message.protocol.tunnel.GhostEnterSuccessCmd;

@Slf4j
public class GhostEnterTask implements Runnable {
    @Getter
    private final PlayerSession playerSession;
    @Getter
    private final GhostEnterCmd ghostEnterCmd;

    public GhostEnterTask(PlayerSession playerSession, GhostEnterCmd ghostEnterCmd) {
        this.playerSession = playerSession;
        this.ghostEnterCmd = ghostEnterCmd;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        if (PlayerManager.getInstance().isPlayerRunning(ghostEnterCmd.getPlayerId())) {
            if (PlayerManager.getInstance().isPlayerExists(ghostEnterCmd.getPlayerId())) {
                log.error("Duplicated Ghost Enter Exception PlayerUid={}", ghostEnterCmd.getPlayerId());
                GhostEnterErrorCmd.Builder builder = GhostEnterErrorCmd.newBuilder();
                builder.setPlayerId(ghostEnterCmd.getPlayerId());
                playerSession.disconnect(builder.build());
                PlayerAsyncTask.submit(ghostEnterCmd.getPlayerId(), p -> p.ghostAgent.destroy());
            } else {
                PlayerData playerData = TunnelUtil.loadPlayerData(ghostEnterCmd.getPlayerData());
                TunnelVisaData visaData = TunnelUtil.loadVisaData(ghostEnterCmd.getVisaData());
                playerData.setLoadOver(false);//关闭load over
                Player player = PlayerManager.getInstance().buildPlayer(playerSession, playerData);
                player.ghostAgent.setVisaData(visaData);
                playerData.setLoadOver(true);//关闭load over
                playerSession.establish(ghostEnterCmd);
                GhostUtil.save();
                GhostEnterSuccessCmd.Builder builder = GhostEnterSuccessCmd.newBuilder();
                builder.setPlayerId(ghostEnterCmd.getPlayerId());
                playerSession.send(builder.build());
            }
        }
        long used = System.currentTimeMillis() - timestamp;
        log.info("Ghost Enter used = {}ms,PlayerUid = {}", used, ghostEnterCmd.getPlayerId());
    }
}
