package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;

import java.util.List;
import java.util.Set;

public enum WorldBuilding {
    Throne(1, 1, WorldMapPointRegulation.getThroneMainId(), WorldMapPointRegulation.getThrone()),
    NorthTower(2, 2, WorldMapPointRegulation.getNorthTowerMainId(), WorldMapPointRegulation.getNorthTower()),
    SouthTower(3, 2, WorldMapPointRegulation.getSouthTowerMainId(), WorldMapPointRegulation.getSouthTower()),
    WestTower(4, 2, WorldMapPointRegulation.getWestTowerMainId(), WorldMapPointRegulation.getWestTower()),
    EastTower(5, 2, WorldMapPointRegulation.getEastTowerMainId(), WorldMapPointRegulation.getEastTower()),
    ;
    public static final Set<Integer> TempleBattleBuildingPoints = ImmutableSet.<Integer>builder()
            .addAll(Throne.getWorldPointIdList())
            .addAll(NorthTower.getWorldPointIdList())
            .addAll(SouthTower.getWorldPointIdList())
            .addAll(WestTower.getWorldPointIdList())
            .addAll(EastTower.getWorldPointIdList())
            .build();
    public static final Set<Integer> TempleBattleBuildingMainPoints = ImmutableSet.of(
            Throne.getWorldPointMainId(),
            NorthTower.getWorldPointMainId(),
            SouthTower.getWorldPointMainId(),
            WestTower.getWorldPointMainId(),
            EastTower.getWorldPointMainId());
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
