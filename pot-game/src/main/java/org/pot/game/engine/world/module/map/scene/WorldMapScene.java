package org.pot.game.engine.world.module.map.scene;

import lombok.NonNull;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.world.WorldManager;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class WorldMapScene extends AbstractScene {
    public static final WorldMapScene singleton = new WorldMapScene();

    public WorldMapScene() {
        super(
                WorldMapScene.class.getSimpleName(),
                WorldMapCityRegulation::new,
                WorldMapMarchRegulation::new,
                WorldMapPointRegulation::new
        );
    }

    public void tick() {
        this.viewManger.tick();
        this.cityManager.tick();
        this.marchManager.tick();
        this.pointManager.tick();
    }

    public void save(boolean async) {
        this.marchManager.save(async);
        this.pointManager.save(async);
    }

    @Override
    public WorldMapCityRegulation getCityRegulation() {
        return (WorldMapCityRegulation) cityRegulation;
    }

    @Override
    public WorldMapMarchRegulation getMarchRegulation() {
        return (WorldMapMarchRegulation) marchRegulation;
    }

    @Override
    public boolean isThreadSafe() {
        return WorldManager.isWorldThread();
    }

    @Override
    public WorldMapPointRegulation getPointRegulation() {
        return (WorldMapPointRegulation) pointRegulation;
    }

    @Override
    public void requireThreadSafe() {
        WorldManager.requireWorldThread();
    }

    @Override
    public void submit(Runnable runnable) {
        WorldManager.getInstance().submit(runnable);
    }

    @Override
    public <T> CompletableFuture<T> submit(Supplier<T> supplier) {
        return WorldManager.getInstance().submit(supplier);
    }

    @Override
    public <T> T execute(Supplier<T> supplier) {
        return WorldManager.getInstance().execute(supplier);
    }

    /**
     * 从地图中心由内向外block带中为一个地图数据随机位置
     */
    public int distributeOutward(@NonNull Function<WorldBlock, Collection<Integer>> pointIdListFunc, @NonNull PointExtraData pointExtraData) {
        requireThreadSafe();
        for (Integer blockBandId : WorldMapPointRegulation.getHighToLowResourceBandIdList()) {
            WorldBand blockBand = WorldMapPointRegulation.getBlockBand(blockBandId);
            if (blockBand == null) {
                continue;
            }
            for (Integer blockId : blockBand.getDisorderlyBlockIds()) {
                WorldBlock block = WorldMapPointRegulation.getBlock(blockId);
                if (block == null) {
                    continue;
                }
                Collection<Integer> pointIdList = pointIdListFunc.apply(block);
                if (CollectionUtil.isEmpty(pointIdList)) {
                    continue;
                }
                int pointId = putPoint(pointIdList, pointExtraData);
                if (pointId != PointUtil.INVALID_POINT_ID) {
                    return pointId;
                }
            }
        }
        return PointUtil.INVALID_POINT_ID;
    }


}
