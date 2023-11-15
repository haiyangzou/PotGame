package org.pot.game.engine.world.module.map.scene;

import lombok.Getter;
import org.pot.common.util.PointUtil;
import org.pot.common.util.RandomUtil;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.CityRegulation;
import org.pot.game.engine.scene.PointManager;
import org.pot.game.engine.world.module.map.born.PlayerBornPhase;
import org.pot.game.engine.world.module.map.born.PlayerBornRule;
import org.pot.game.engine.world.module.map.clean.WorldMapCityCleaner;

import java.util.List;

public class WorldMapCityRegulation extends CityRegulation {
    @Getter
    private final WorldMapCityCleaner cleaner = new WorldMapCityCleaner();

    public WorldMapCityRegulation(AbstractScene scene) {
        super(scene);
    }


    @Override
    protected void save(boolean async) {
        PlayerBornRule.getInstance().save();
        cleaner.save();
    }

    @Override
    protected void init() {
        PlayerBornRule.getInstance().init();
        cleaner.init();
    }

    @Override
    protected void tick() {
        cleaner.tick();
    }

    @Override
    protected int randomPutCity(PointCityData city) {
        List<Integer> bornBandIds = RandomUtil.randomSequence(
                PlayerBornPhase.PHASE_3.getBornMinBandId(), PlayerBornPhase.PHASE_3.getBornMaxBandId()
        );
        PointManager pointManager = scene.getPointManager();
        for (Integer bornBandId : bornBandIds) {
            WorldBand band = WorldMapPointRegulation.getResourceBand(bornBandId);
            List<Integer> disorderlyBlockIds = band.getDisorderlyBlockIds();
            for (Integer blockId : disorderlyBlockIds) {
                WorldBlock block = WorldMapPointRegulation.getBlock(blockId);
                int pointId = pointManager.allocateRandomLocation(block.getPointIds(), city);
                if (pointId != PointUtil.INVALID_POINT_ID) {
                    return pointId;
                }
            }
        }
        return PointUtil.INVALID_POINT_ID;
    }
}
