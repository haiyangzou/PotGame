package org.pot.game.engine.world.module.map.monster;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.point.PointMonsterData;
import org.pot.game.engine.scene.PointListener;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;
import org.pot.game.engine.world.module.var.WorldVarDef;
import org.pot.game.resource.map.MapLimitLevelConfig;
import org.pot.game.resource.map.MonsterRefresh;
import org.pot.game.resource.map.SlgMonsterInfoConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

public class WorldMonsterModule extends AbstractWorldModule implements PointListener {
    public static WorldMonsterModule getInstance() {
        return WorldModuleType.WORLD_MONSTER.getInstance();
    }

    private final Set<Integer> timeoutMonsterPointIds = new CopyOnWriteArraySet<>();

    private final Map<Integer, WorldMonsterBlockRefreshInfo> blockMonsterRefreshInfoMap = new ConcurrentHashMap<>();

    private final RunSignal refreshSignal = new RunSignal(TimeUnit.SECONDS.toMillis(1));
    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));

    @Override
    public CompletableFuture<?> init() {
        WorldMapScene.instance.getPointManager().addListener(this);
        loadMonsterInfo();
        return null;
    }

    private void loadMonsterInfo() {
        List<Integer> list = WorldVarDef.StringVar.TimeoutMonsterInfo.parseJson(new TypeReference<List<Integer>>() {
        });
        if (list != null) {
            timeoutMonsterPointIds.addAll(list);
        }
        Map<Integer, WorldMonsterBlockRefreshInfo> map = WorldVarDef.StringVar.BlockMonsterRefreshInfo.parseJson(
                new TypeReference<Map<Integer, WorldMonsterBlockRefreshInfo>>() {
                }
        );
        if (map != null) {
            blockMonsterRefreshInfoMap.putAll(map);
        }
    }

    @Override
    public void tick() {
        if (refreshSignal.signal()) {
            clearTimeOutMonster();
            refreshWorldMonster();
        }
        saveSignal.run(this::saveMonsterInfo);
    }

    private void clearTimeOutMonster() {

    }

    private void refreshWorldMonster() {

    }

    @Override
    public void shutdown() {
        saveMonsterInfo();
    }

    private void saveMonsterInfo() {
        WorldVarDef.StringVar.TimeoutMonsterInfo.update(JsonUtil.toJson(timeoutMonsterPointIds));
        WorldVarDef.StringVar.BlockMonsterRefreshInfo.update(JsonUtil.toJson(blockMonsterRefreshInfoMap));
    }


    @Override
    public void onPointAdded(WorldPoint worldPoint) {
        PointExtraData pointExtraData = worldPoint.getRawExtraData();
        if (pointExtraData instanceof PointMonsterData) {
            if (((PointMonsterData) pointExtraData).getTimeout() > 0) {
                timeoutMonsterPointIds.add(worldPoint.getId());
            }
        }
    }

    @Override
    public void onPointRemoved(WorldPoint worldPoint) {
        if (worldPoint.getType() == PointType.MONSTER) {
            timeoutMonsterPointIds.remove(worldPoint.getId());
        }
    }

    private Map<Integer, Integer> calcMonsterRate(MonsterRefresh monsterRefresh) {
        Map<Integer, Integer> rate = Maps.newHashMap(monsterRefresh.getRate());
        MapLimitLevelConfig.getInstance().getMaxMonsterLevel(GameEngine.getInstance().getOpenedDays());
        SlgMonsterInfoConfig.getInstance();
    }
}
