package org.pot.game.engine.world.module.map.monster;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.*;
import org.pot.game.engine.GameConstants;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.point.PointMonsterData;
import org.pot.game.engine.scene.PointListener;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;
import org.pot.game.engine.world.module.map.scene.WorldMapPointRegulation;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;
import org.pot.game.engine.world.module.var.WorldVarDef;
import org.pot.game.resource.map.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@Slf4j
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
        long now = System.currentTimeMillis();
        List<Integer> removes = new ArrayList<>();
        for (Integer pointId : timeoutMonsterPointIds) {
            WorldPoint point = WorldMapScene.instance.getPoint(pointId);
            if (point == null) {
                removes.add(pointId);
                continue;
            }
            PointMonsterData pointMonsterData = point.getExtraData(PointMonsterData.class);
            if (pointMonsterData == null) {
                removes.add(pointId);
                continue;
            }
            if (now > pointMonsterData.getTimeout() && pointMonsterData.getMarchIdList().isEmpty()) {
                WorldMapScene.instance.removePoint(pointId);
                removes.add(pointId);
            }
        }
        if (!removes.isEmpty()) {
            removes.forEach(timeoutMonsterPointIds::remove);
        }
    }

    private void refreshWorldMonster() {
        boolean changed = false;
        int mapRefreshDayId = MapRefreshDayConfig.getIdWithServerOpenDays(GameEngine.getInstance().getOpenedDays());
        for (Integer blockBandId : WorldMapPointRegulation.getHighToLowResourceBandIdList()) {
            changed = refreshBandMonster(mapRefreshDayId, blockBandId) || changed;
        }
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

    private boolean refreshBandMonster(int mapRefreshDayId, int blockBandId) {
        boolean changed = false;
        for (Integer blockId : WorldMapPointRegulation.getBlockBand(blockBandId).getBlockIds()) {
            changed = refreshBlockMonster(mapRefreshDayId, blockBandId, blockId) || changed;
        }
        return changed;
    }

    private boolean refreshBlockMonster(int mapRefreshDayId, int blockBandId, Integer blockId) {
        WorldMonsterBlockRefreshInfo worldMonsterBlockRefreshInfo = blockMonsterRefreshInfoMap.get(blockId);
        if (worldMonsterBlockRefreshInfo == null) {
            addBlockMonster(mapRefreshDayId, blockBandId, blockId);
            return true;
        }
        if (worldMonsterBlockRefreshInfo.getRefreshTimestamp() <= System.currentTimeMillis()) {
            if (mapRefreshDayId != worldMonsterBlockRefreshInfo.getRefreshDayId()) {
                clearBockMonster(mapRefreshDayId, blockBandId, blockId);
            }
            addBlockMonster(mapRefreshDayId, blockBandId, blockId);
            return true;
        }
        return false;
    }

    private void clearBockMonster(int mapRefreshDayId, int blockBandId, Integer blockId) {
        List<Integer> blockPointIds = WorldMapPointRegulation.getBlock(blockId).getPointIds();
        List<WorldPoint> currentMonsterPoints = WorldMapScene.instance.getPointManager().getMainPoints(blockPointIds, PointType.MONSTER);
        currentMonsterPoints.removeIf(worldPoint -> {
            PointMonsterData monsterData = worldPoint.getExtraData(PointMonsterData.class);
            return monsterData != null && monsterData.getMarchIdList().isEmpty();
        });
        //移除世界上的空閒的資源田
        currentMonsterPoints.forEach(WorldMapScene.instance::removePoint);
        log.info("clear block monster");
    }

    private void addBlockMonster(int mapRefreshDayId, int blockBandId, Integer blockId) {
        updateWorldMonsterBLockRefreshInfo(mapRefreshDayId, blockBandId, blockId);
        MonsterRefresh monsterRefresh = MonsterRefreshConfig.getInstance().getMonsterRefresh(mapRefreshDayId, blockBandId);
        if (monsterRefresh == null) {
            return;
        }
        Map<Integer, Integer> rate = calcMonsterRate(monsterRefresh);
        if (MapUtil.isEmpty(rate)) {
            return;
        }
        List<Integer> blockPointIds = WorldMapPointRegulation.getBlock(blockId).getPointIds();

        //獲取坐標中可建造的做標書
        int canBuildPointCount = WorldMapScene.instance.getPointManager().getCanBuildPointCount(blockPointIds);
        int currentCount = WorldMapScene.instance.getPointManager().getMainPoints(blockPointIds, PointType.MONSTER).size();

        canBuildPointCount = canBuildPointCount + (currentCount + PointType.MONSTER.getArea());
        double percent = GameConstants.BLOCK_MONSTER_PERCENT;
        int totalCount = (int) (canBuildPointCount * percent / PointType.MONSTER.getArea());
        int refreshCount = Math.max(0, totalCount - currentCount);
        int realCount = 0;
        for (int i = 0; i < refreshCount; i++) {
            Integer monsterId = RandomUtil.randomOneKey(rate);
            if (monsterId == null) {
                break;
            }
            PointMonsterData monster = new PointMonsterData(monsterId);
            int mainPointId = WorldMapScene.instance.putPoint(blockPointIds, monster);
            if (mainPointId == PointUtil.INVALID_POINT_ID) {
                break;
            } else {
                realCount++;
            }
        }
        if (refreshCount > 0 && realCount > 0) {
            log.info("add block monster,DayId={}blockBandId={},blockId={},total={},refresh={},real={}", mapRefreshDayId, blockBandId, blockId, totalCount, refreshCount, realCount);
        }
    }

    /**
     * 更新刷新信息
     *
     * @param mapRefreshDayId
     * @param blockBandId
     * @param blockId
     */
    private void updateWorldMonsterBLockRefreshInfo(int mapRefreshDayId, int blockBandId, Integer blockId) {
        long intervalMillis = GameConstants.BLOCK_MONSTER_REFRESH_MILLIS;
        long offsetMillis = blockId * (intervalMillis / WorldMapPointRegulation.getBlocks().size());
        long refreshTimestamp = ExDateTimeUtil.getPrevDayStart() - offsetMillis;
        while (refreshTimestamp <= System.currentTimeMillis()) {
            refreshTimestamp += intervalMillis;
        }
        blockMonsterRefreshInfoMap.put(blockId, new WorldMonsterBlockRefreshInfo(blockId, mapRefreshDayId, refreshTimestamp));
    }

    private Map<Integer, Integer> calcMonsterRate(MonsterRefresh monsterRefresh) {
        Map<Integer, Integer> rate = Maps.newHashMap(monsterRefresh.getRate());
        int maxMonsterLevel = MapLimitLevelConfig.getInstance().getMaxMonsterLevel(GameEngine.getInstance().getOpenedDays());
        List<SlgMonsterInfo> slgMonsterInfoList = SlgMonsterInfoConfig.getInstance().getSpecList();
        for (SlgMonsterInfo slgMonsterInfo : slgMonsterInfoList) {
            if (slgMonsterInfo.getLevel() > maxMonsterLevel) {
                rate.remove(slgMonsterInfo.getId());
            }
        }
        return rate;
    }
}
