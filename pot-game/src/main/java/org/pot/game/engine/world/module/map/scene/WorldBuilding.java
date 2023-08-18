package org.pot.game.engine.world.module.map.scene;

import lombok.Getter;

import java.util.List;

public enum WorldBuilding {
    Throne(1, 1, WorldMapPointRegulation.getThroneMainId(), WorldMapPointRegulation.getThrone()),
    ;
    @Getter
    private final int buildingId;
    @Getter
    private final int buildingType;
    @Getter
    private final int worldPointMainId;
    @Getter
    private final List<Integer> worldPointIdList;

    WorldBuilding(int buildingId, int buildingType, int worldPointMainId, List<Integer> worldPointIdList) {
        this.buildingId = buildingId;
        this.buildingType = buildingType;
        this.worldPointMainId = worldPointMainId;
        this.worldPointIdList = worldPointIdList;
    }
}
