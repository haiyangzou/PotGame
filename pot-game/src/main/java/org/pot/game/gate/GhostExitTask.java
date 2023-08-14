package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAsyncTask;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.player.PlayerState;
import org.pot.message.protocol.tunnel.GhostExitCmd;
import org.pot.message.protocol.tunnel.GhostExitErrorCmd;
import org.pot.message.protocol.tunnel.GhostExitSuccessCmd;

@Slf4j
public class GhostExitTask implements Runnable {
    @Getter
    private final PlayerSession playerSession;
    @Getter
    private final GhostExitCmd ghostExitCmd;

    public GhostExitTask(PlayerSession playerSession, GhostExitCmd ghostExitCmd) {
        this.playerSession = playerSession;
        this.ghostExitCmd = ghostExitCmd;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        if (PlayerManager.getInstance().isPlayerRunning(ghostExitCmd.getPlayerId())) {
            Player player = PlayerManager.fetchPlayer(ghostExitCmd.getPlayerId());
            if (player == null || player.getState().get() != PlayerState.running || !player.ghostAgent.isGhost() || player.ghostAgent.isDestroyed()) {
                //不存在跨服玩家
                log.error("Not Exists Ghost Exit Exception, PlayerUid={}", ghostExitCmd.getPlayerId());
                GhostExitErrorCmd.Builder builder = GhostExitErrorCmd.newBuilder();
                builder.setPlayerId(ghostExitCmd.getPlayerId());
                playerSession.send(builder.build());
                PlayerAsyncTask.submit(ghostExitCmd.getPlayerId(), p -> p.ghostAgent.destroy());
            } else {
                PlayerSession oldPlayerSession = player.setPlayerSession(playerSession);
                if (oldPlayerSession != null && oldPlayerSession != playerSession) {
                    oldPlayerSession.disconnect(CommonErrorCode.LOGIN_KICK);
                }
                playerSession.establish(ghostExitCmd);
                GhostExitSuccessCmd.Builder builder = GhostExitSuccessCmd.newBuilder();
                builder.setPlayerId(ghostExitCmd.getPlayerId());
                playerSession.send(builder.build());
            }
        }
        long used = System.currentTimeMillis() - timestamp;
        log.info("Ghost Exit used = {}ms,PlayerUid = {}", used, ghostExitCmd.getPlayerId());
    }
}
