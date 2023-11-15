package org.pot.game.engine.world.module.map;

import lombok.Getter;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.scene.PointManager;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;

import java.util.concurrent.TimeUnit;

public class WorldMapModule extends AbstractWorldModule {
    public static WorldMapModule singleton() {
        return WorldModuleType.WORLD_MAP.getModule();
    }

    @Getter
    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));


    @Override
    public void init() {
        WorldMapScene.singleton.init();
    }

    @Override
    public void initPlayerData() {
        PointManager pointManager = WorldMapScene.singleton.getPointManager();
        for (WorldPoint point : pointManager.getPoints()) {
            if (!point.isMainPoint()) continue;
            if (point.getRawExtraData() == null) continue;
            if (point.getType() == PointType.CITY) {
                PointCityData pointCityData = (PointCityData) point.getRawExtraData();
                //拉起玩家
                PlayerManager.fetchPlayer(pointCityData.getPlayerId());
            }
        }
    }

    @Override
    public void tick() {
        WorldMapScene.singleton.tick();
        saveSignal.run(() -> WorldMapScene.singleton.save(true));
    }

    @Override
    public void shutdown() {
        WorldMapScene.singleton.save(false);
    }
}
