package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;
import org.pot.message.protocol.tunnel.GhostKeepAliveCmd;

@Slf4j
public class GhostKeepAliveTask implements Runnable {
    @Getter
    private final PlayerSession playerSession;
    @Getter
    private final GhostKeepAliveCmd ghostKeepAliveCmd;

    public GhostKeepAliveTask(PlayerSession playerSession, GhostKeepAliveCmd ghostKeepAliveCmd) {
        this.playerSession = playerSession;
        this.ghostKeepAliveCmd = ghostKeepAliveCmd;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        if (PlayerManager.getInstance().isPlayerRunning(ghostKeepAliveCmd.getPlayerId())) {
            Player player = PlayerManager.fetchPlayer(ghostKeepAliveCmd.getPlayerId());
            if (player != null) {
                PlayerSession oldPlayerSession = player.setPlayerSession(playerSession);
                if (oldPlayerSession != null && oldPlayerSession != playerSession) {
                    oldPlayerSession.disconnect(CommonErrorCode.LOGIN_KICK);
                }
                if (playerSession.isOnline()) {
                    playerSession.recv(new FramePlayerMessage(ghostKeepAliveCmd.getPlayerId(), ghostKeepAliveCmd));
                } else {
                    playerSession.initialize();
                    playerSession.establish(ghostKeepAliveCmd);
                }
            }
        }
        long used = System.currentTimeMillis() - timestamp;
        log.info("Ghost KeepAlive used = {}ms,PlayerUid = {}", used, ghostKeepAliveCmd.getPlayerId());
    }
}
