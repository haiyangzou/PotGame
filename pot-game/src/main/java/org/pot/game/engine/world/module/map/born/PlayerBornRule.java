package org.pot.game.engine.world.module.map.born;

import lombok.Getter;
import org.pot.common.util.JsonUtil;
import org.pot.game.engine.world.module.map.scene.WorldBlock;
import org.pot.game.engine.world.module.map.scene.WorldMapPointRegulation;
import org.pot.game.engine.world.module.var.WorldVarDef;

import java.util.List;

public class PlayerBornRule {
    @Getter
    private static final PlayerBornRule instance = new PlayerBornRule();
    private volatile PlayerBornInfo playerBornInfo;

    public void init() {
        playerBornInfo = JsonUtil.parseJson(WorldVarDef.StringVar.PlayerBornRulePhaseInfo.value(null), PlayerBornInfo.class);
        if (playerBornInfo == null) {
            playerBornInfo = new PlayerBornInfo(PlayerBornPhase.PHASE_1);
        }
    }

    public void save() {
        WorldVarDef.StringVar.PlayerBornRulePhaseInfo.update(JsonUtil.toJson(playerBornInfo));
    }

    public int bornPlayer(long playerId) {
        return executePhase(playerId);
    }

    private int executePhase(long playerId) {
        switch (playerBornInfo.getPlayerBornPhase()) {
            case PHASE_1:
                return executeCommonPhase(playerId, PlayerBornPhase.PHASE_2);
            case PHASE_2:
                return executeCommonPhase(playerId, PlayerBornPhase.PHASE_3);
            case PHASE_3:
            default:
                return executeFinalPhase(playerId);
        }
    }

    private int executeCommonPhase(long playerId, PlayerBornPhase nextPhase) {
        PlayerBornPhase playerBornPhase = playerBornInfo.getPlayerBornPhase();
        List<Integer> blocks = playerBornInfo.getBlocks();
        for (int i = playerBornInfo.getCurrentIndex(); i >= 0 && i < blocks.size(); i++) {
            playerBornInfo.setCurrentIndex(i);
            Integer blockId = blocks.get(i);
            WorldBlock block = WorldMapPointRegulation.getBlock(blockId);
        }
        return executePhase(playerId);
    }

    private int executeFinalPhase(long playerId) {
        return 0;
    }
}
