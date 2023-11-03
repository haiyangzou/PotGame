package org.pot.game.engine.player.module.ghost;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.gate.GhostUtil;
import org.pot.game.gate.TunnelUtil;
import org.pot.game.gate.TunnelVisaData;
import org.pot.game.persistence.entity.PlayerGhostEntity;
import org.pot.message.protocol.tunnel.GhostUpdateCmd;

@Slf4j
@Getter
public class PlayerGhostAgent extends PlayerAgentAdapter {
    private volatile boolean exited;
    private volatile boolean destroyed;
    @Setter
    private volatile TunnelVisaData visaData;

    public PlayerGhostAgent(Player player) {
        super(player);
    }

    public boolean isGhost() {
        return player.getServerId() != GameServerInfo.getServerId();
    }

    public void destroy() {
        destroyed = true;
        GhostUtil.save(toEntity());
    }

    public void delete() {
        new PlayerData(player.getUid()).asyncDelete(() -> {
            GhostUtil.delete(player.getUid());
            log.info("Ghost Player Delete Success! uid = {}", player.getUid());
        }, () -> {
            log.info("Ghost Player Delete Fail! uid = {}", player.getUid());
        });
    }

    public void update(PlayerData playerData) {
        if (!isGhost()) return;
        if (destroyed) return;
        GhostUtil.save(toEntity());
        GhostUpdateCmd.Builder builder = GhostUpdateCmd.newBuilder().setPlayerId(player.getUid());
        builder.setPlayerData(TunnelUtil.savePlayerData(playerData));
        player.sendMessage(builder.build());
    }

    public PlayerGhostEntity toEntity() {
        if (visaData == null) {
            return null;
        }
        PlayerGhostEntity entity = new PlayerGhostEntity();
        entity.setPlayerId(player.getUid());
        return entity;
    }
}
