package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.player.PlayerManager;
import org.pot.message.protocol.ProtocolPrinter;
import org.pot.message.protocol.login.LoginDataS2S;

@Slf4j
public class PlayerLoginTask implements Runnable {
    @Getter
    private final PlayerSession playerSession;
    @Getter
    private final LoginDataS2S loginDataS2S;

    public PlayerLoginTask(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        this.playerSession = playerSession;
        this.loginDataS2S = loginDataS2S;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        this.execute();
        long used = System.currentTimeMillis() - timestamp;
        log.info("Player Login used={}ms,request={}", used, ProtocolPrinter.printJson(loginDataS2S));
    }

    private void execute() {
        final String account = loginDataS2S.getLoginReqC2S().getAccount();
        final long uid = loginDataS2S.getGameUid();
        final String ip = loginDataS2S.getIp();
        try {
            if (GameEngine.getInstance().getConfig().getTcpFirewall().isDeniedIpv4(ip)) {
                log.info("");
                playerSession.disconnect(CommonErrorCode.CONNECT_FAIL);
                return;
            }
            if (TunnelManager.instance.reconnect(playerSession, loginDataS2S)) {
                log.info("Player Login Tunnel Reconnected,ip={},account={},uid={}", ip, account, uid);
                return;
            }
            LoginDataS2S finalLoginDataS2S = PlayerManager.getInstance().loginPlayer(playerSession, loginDataS2S);
            if (finalLoginDataS2S == null) {
                log.info("");
                playerSession.disconnect(CommonErrorCode.LOGIN_FAIL);
            } else {
                playerSession.establish(finalLoginDataS2S);
            }
        } catch (Throwable ex) {
            log.error("Player Login Error.ip={},account={},uid={}", ip, account, uid, ex);
            playerSession.disconnect(CommonErrorCode.LOGIN_FAIL);
        }
    }
}
