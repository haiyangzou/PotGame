package org.pot.game.engine.player.module.drop;

import lombok.Getter;
import org.pot.game.engine.drop.DropRecord;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.persistence.entity.DropRecordEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerDropAgent extends PlayerAgentAdapter {
    private Map<String, DropRecord> dropRecord = new HashMap<>();

    public PlayerDropAgent(Player player) {
        super(player);
    }

    protected void initData(PlayerData playerData) {
        saveData(playerData);
    }

    protected void loadData(PlayerData playerData) {
        if (playerData.getDropRecordEntity() == null) {
            initData(playerData);
            return;
        }
        DropRecordEntity dropRecordEntity = playerData.getDropRecordEntity();
        this.dropRecord = dropRecordEntity.getDetail();
    }

    protected void saveData(PlayerData playerData) {
        DropRecordEntity dropRecordEntity = new DropRecordEntity();
        dropRecordEntity.setId(this.player.getUid());
        dropRecordEntity.setDetail(this.dropRecord);
        playerData.setDropRecordEntity(dropRecordEntity);
    }
}
