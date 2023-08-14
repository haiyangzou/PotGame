package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAsyncTask;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.player.PlayerState;
import org.pot.message.protocol.tunnel.GhostDestroyCmd;
import org.pot.message.protocol.tunnel.GhostDestroyErrorCmd;
import org.pot.message.protocol.tunnel.GhostDestroySuccessCmd;

@Slf4j
public class GhostDestroyTask implements Runnable {
    @Getter
    private final PlayerSession playerSession;
    @Getter
    private final GhostDestroyCmd ghostDestroyCmd;

    public GhostDestroyTask(PlayerSession playerSession, GhostDestroyCmd ghostDestroyCmd) {
        this.playerSession = playerSession;
        this.ghostDestroyCmd = ghostDestroyCmd;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        Player player = PlayerManager.fetchPlayer(ghostDestroyCmd.getPlayerId());
        if (player == null || player.getState().get() != PlayerState.running || !player.ghostAgent.isGhost() || player.ghostAgent.isDestroyed()) {
            //不存在跨服玩家
            log.error("Not Exists Ghost Destroy Exception, PlayerUid={}", ghostDestroyCmd.getPlayerId());
            GhostDestroyErrorCmd.Builder builder = GhostDestroyErrorCmd.newBuilder();
            builder.setPlayerId(ghostDestroyCmd.getPlayerId());
            playerSession.disconnect(builder.build());
        } else {
            GhostDestroySuccessCmd.Builder builder = GhostDestroySuccessCmd.newBuilder();
            builder.setPlayerId(ghostDestroyCmd.getPlayerId());
            playerSession.send(builder.build());
        }
        PlayerAsyncTask.submit(ghostDestroyCmd.getPlayerId(), p -> p.ghostAgent.destroy());
        long used = System.currentTimeMillis() - timestamp;
        log.info("Ghost Destroy used = {}ms,PlayerUid = {}", used, ghostDestroyCmd.getPlayerId());
    }
}
