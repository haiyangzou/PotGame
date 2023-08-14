package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAsyncTask;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.player.PlayerState;
import org.pot.message.protocol.tunnel.GhostReconnectCmd;
import org.pot.message.protocol.tunnel.GhostReconnectErrorCmd;
import org.pot.message.protocol.tunnel.GhostReconnectSuccessCmd;

@Slf4j
public class GhostReconnectTask implements Runnable {
    @Getter
    private final PlayerSession playerSession;
    @Getter
    private final GhostReconnectCmd ghostReconnectCmd;

    public GhostReconnectTask(PlayerSession playerSession, GhostReconnectCmd ghostReconnectCmd) {
        this.playerSession = playerSession;
        this.ghostReconnectCmd = ghostReconnectCmd;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        Player player = PlayerManager.fetchPlayer(ghostReconnectCmd.getPlayerId());
        if (player == null || player.getState().get() != PlayerState.running || !player.ghostAgent.isGhost() || player.ghostAgent.isDestroyed()) {
            log.error("Not Exists Ghost Exit Exception, PlayerUid={}", ghostReconnectCmd.getPlayerId());
            GhostReconnectErrorCmd.Builder builder = GhostReconnectErrorCmd.newBuilder();
            builder.setPlayerId(ghostReconnectCmd.getPlayerId());
            playerSession.disconnect(builder.build());
            PlayerAsyncTask.submit(ghostReconnectCmd.getPlayerId(), p -> p.ghostAgent.destroy());
        } else {
            PlayerSession oldPlayerSession = player.setPlayerSession(playerSession);
            if (oldPlayerSession != null && oldPlayerSession != playerSession) {
                oldPlayerSession.disconnect(CommonErrorCode.LOGIN_KICK);
            }
            playerSession.establish(ghostReconnectCmd);
            GhostReconnectSuccessCmd.Builder builder = GhostReconnectSuccessCmd.newBuilder();
            builder.setPlayerId(ghostReconnectCmd.getPlayerId());
            playerSession.send(builder.build());
        }
        long used = System.currentTimeMillis() - timestamp;
        log.info("Ghost Reconnect used = {}ms,PlayerUid = {}", used, ghostReconnectCmd.getPlayerId());
    }
}
