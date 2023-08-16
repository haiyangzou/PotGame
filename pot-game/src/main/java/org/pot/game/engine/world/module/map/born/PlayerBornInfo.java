package org.pot.game.engine.world.module.map.born;

import lombok.Data;
import org.pot.game.engine.world.module.map.scene.WorldBand;
import org.pot.game.engine.world.module.map.scene.WorldMapPointRegulation;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerBornInfo {
    private int phaseId;
    private int currentIndex;
    private List<Integer> blocks;

    public PlayerBornInfo(PlayerBornPhase playerBornPhase) {
        this.setPhase(playerBornPhase);
    }

    void setPhase(PlayerBornPhase playerBornPhase) {
        this.currentIndex = 0;
        this.blocks = new ArrayList<>();
        this.phaseId = playerBornPhase.getId();
        int bornMinBandId = playerBornPhase.getBornMinBandId();
        int bornMaxBandId = playerBornPhase.getBornMaxBandId();
        //出生资源带有序，随机打乱每个资源带的出生block顺序
        for (int bandId = bornMinBandId; bandId <= bornMaxBandId; bandId++) {
            WorldBand resourceBand = WorldMapPointRegulation.getResourceBand(bandId);

        }
    }

    PlayerBornPhase getPlayerBornPhase() {
        return PlayerBornPhase.find(phaseId);
    }
}
