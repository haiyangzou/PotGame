package org.pot.game.engine.world.module.map;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.pot.common.util.MathUtil;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.scene.PointManager;
import org.pot.game.engine.scene.PointRegulation;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;
import org.pot.game.engine.world.module.map.born.PlayerBornRule;
import org.pot.game.engine.world.module.map.clean.WorldMapCleaner;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WorldMapModule extends AbstractWorldModule {
    public static WorldMapModule getInstance() {
        return WorldModuleType.WORLD_MAP.getInstance();
    }

    @Getter
    private final WorldMapCleaner cleaner = new WorldMapCleaner();
    @Getter
    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));

    private final ImmutableList<RunSignal> validateSignals = ImmutableList.of(
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)));

    @Override
    public CompletableFuture<?> init() {
        PlayerBornRule.getInstance().init();
        WorldMapScene.singleton.init();
        cleaner.init();
        return null;
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
        cleaner.tick();
        saveSignal.run(() -> this.save(true));
        for (int i = 0; i < validateSignals.size() - 1; i++) {
            if (validateSignals.get(i).signal()) validatePoint(i);
        }
    }

    @Override
    public void shutdown() {
        this.save(false);
    }

    private void validatePoint(int index) {
        PointRegulation regulation = WorldMapScene.singleton.getPointRegulation();
        int yStep = MathUtil.divideAndCeil(regulation.getMaxY(), validateSignals.size());
        int yStart = index * yStep;
        int yEnd = yStart + yStep;
        for (int x = 0; x < regulation.getMaxX(); x++) {
            for (int y = yStart; y < yEnd; y++) {
                if (regulation.isValidCoordinate(x, y)) {
                    WorldPoint p = WorldMapScene.singleton.getPoint(x, y);
                    if (p != null) {
                        p.validate();
                    }
                }
            }
        }
    }

    private void save(boolean async) {
        PlayerBornRule.getInstance().save();
        cleaner.save();
        PointManager pointManager = WorldMapScene.singleton.getPointManager();
        Collection<WorldPoint> worldPoints = pointManager.getPoints();
        if (worldPoints.isEmpty()) return;
        Runnable runnable = () -> {

        };
        if (async) {
            GameEngine.getInstance().getAsyncExecutor().execute(runnable);
        } else {
            runnable.run();
        }
    }
}
