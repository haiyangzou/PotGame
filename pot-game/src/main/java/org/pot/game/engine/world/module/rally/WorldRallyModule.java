package org.pot.game.engine.world.module.rally;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.*;
import org.pot.game.engine.GameConstants;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.point.PointRallyData;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WorldRallyModule extends AbstractWorldModule implements PointListener {
    public static WorldRallyModule getInstance() {
        return WorldModuleType.WORLD_RALLY.getInstance();
    }

    private final Set<Integer> timeoutRallyPointIds = new CopyOnWriteArraySet<>();

    private final Map<Integer, WorldRallyBlockRefreshInfo> blockRallyRefreshInfoMap = new ConcurrentHashMap<>();

    private final RunSignal refreshSignal = new RunSignal(TimeUnit.SECONDS.toMillis(1));
    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));

    @Override
    public void init() {
        WorldMapScene.singleton.getPointManager().addListener(this);
        loadRallyInfo();
    }

    private void loadRallyInfo() {
        List<Integer> list = WorldVarDef.StringVar.TimeoutRallyInfo.parseJson(new TypeReference<List<Integer>>() {
        });
        if (list != null) {
            timeoutRallyPointIds.addAll(list);
        }
        Map<Integer, WorldRallyBlockRefreshInfo> map = WorldVarDef.StringVar.BlockRallyRefreshInfo.parseJson(
                new TypeReference<Map<Integer, WorldRallyBlockRefreshInfo>>() {
                }
        );
        if (map != null) {
            blockRallyRefreshInfoMap.putAll(map);
        }
    }

    @Override
    public void tick() {
        if (refreshSignal.signal()) {
            clearTimeOutRally();
            refreshWorldRally();
        }
        saveSignal.run(this::saveRallyInfo);
    }

    private void clearTimeOutRally() {
        long now = System.currentTimeMillis();
        List<Integer> removes = new ArrayList<>();
        for (Integer pointId : timeoutRallyPointIds) {
            WorldPoint point = WorldMapScene.singleton.getPoint(pointId);
            if (point == null) {
                removes.add(pointId);
                continue;
            }
            PointRallyData pointRallyData = point.getExtraData(PointRallyData.class);
            if (pointRallyData == null) {
                removes.add(pointId);
                continue;
            }
            if (now > pointRallyData.getTimeout() && pointRallyData.getMarchIdList().isEmpty()) {
                if (!WorldMapScene.singleton.getMarchManager().getWarManager().existLairWar(pointId)) {
                    WorldMapScene.singleton.removePoint(pointId);
                    removes.add(pointId);
                }
            }
        }
        if (!removes.isEmpty()) {
            removes.forEach(timeoutRallyPointIds::remove);
        }
    }

    private void refreshWorldRally() {
        boolean changed = false;
        int mapRefreshDayId = MapRefreshDayConfig.getIdWithServerOpenDays(GameEngine.getInstance().getOpenedDays());
        for (Integer blockBandId : WorldMapPointRegulation.getHighToLowResourceBandIdList()) {
            changed = refreshBandRally(mapRefreshDayId, blockBandId) || changed;
        }
    }

    @Override
    public void shutdown() {
        saveRallyInfo();
    }

    private void saveRallyInfo() {
        WorldVarDef.StringVar.TimeoutRallyInfo.update(JsonUtil.toJson(timeoutRallyPointIds));
        WorldVarDef.StringVar.BlockRallyRefreshInfo.update(JsonUtil.toJson(blockRallyRefreshInfoMap));
    }


    @Override
    public void onPointAdded(WorldPoint worldPoint) {
        PointExtraData pointExtraData = worldPoint.getRawExtraData();
        if (pointExtraData instanceof PointRallyData) {
            if (((PointRallyData) pointExtraData).getTimeout() > 0) {
                timeoutRallyPointIds.add(worldPoint.getId());
            }
        }
    }

    @Override
    public void onPointRemoved(WorldPoint worldPoint) {
        if (worldPoint.getType() == PointType.RALLY) {
            timeoutRallyPointIds.remove(worldPoint.getId());
        }
    }

    private boolean refreshBandRally(int mapRefreshDayId, int blockBandId) {
        boolean changed = false;
        for (Integer blockId : WorldMapPointRegulation.getBlockBand(blockBandId).getBlockIds()) {
            changed = refreshBlockRally(mapRefreshDayId, blockBandId, blockId) || changed;
        }
        return changed;
    }

    private boolean refreshBlockRally(int mapRefreshDayId, int blockBandId, Integer blockId) {
        WorldRallyBlockRefreshInfo worldRallyBlockRefreshInfo = blockRallyRefreshInfoMap.get(blockId);
        if (worldRallyBlockRefreshInfo == null) {
            addBlockRally(mapRefreshDayId, blockBandId, blockId);
            return true;
        }
        if (worldRallyBlockRefreshInfo.getRefreshTimestamp() <= System.currentTimeMillis()) {
            if (mapRefreshDayId != worldRallyBlockRefreshInfo.getRefreshDayId()) {
                clearBockRally(mapRefreshDayId, blockBandId, blockId);
            }
            addBlockRally(mapRefreshDayId, blockBandId, blockId);
            return true;
        }
        return false;
    }

    private void clearBockRally(int mapRefreshDayId, int blockBandId, Integer blockId) {
        List<Integer> blockPointIds = WorldMapPointRegulation.getBlock(blockId).getPointIds();
        List<WorldPoint> currentRallyPoints = WorldMapScene.singleton.getPointManager().getMainPoints(blockPointIds, PointType.RALLY);
        currentRallyPoints.removeIf(worldPoint -> {

            PointRallyData pointRallyData = worldPoint.getExtraData(PointRallyData.class);
            if (pointRallyData != null) {
                if (pointRallyData.getMarchIdList().isEmpty()) {
                    return true;
                }
                if (!WorldMapScene.singleton.getMarchManager().getWarManager().existLairWar(worldPoint.getId())) {
                    return true;
                }
            }
            return false;
        });
        //移除世界上的空閒的資源田
        currentRallyPoints.forEach(WorldMapScene.singleton::removePoint);
        log.info("clear block rally");
    }

    private void addBlockRally(int mapRefreshDayId, int blockBandId, Integer blockId) {
        updateWorldRallyBlockRefreshInfo(mapRefreshDayId, blockBandId, blockId);
        RallyRefresh rallyRefresh = RallyRefreshConfig.getInstance().getRallyRefresh(mapRefreshDayId, blockBandId);
        if (rallyRefresh == null) {
            return;
        }
        Map<Integer, Integer> rate = calcRallyRate(rallyRefresh);
        if (MapUtil.isEmpty(rate)) {
            return;
        }
        List<Integer> blockPointIds = WorldMapPointRegulation.getBlock(blockId).getPointIds();

        //獲取坐標中可建造的做標書
        int canBuildPointCount = WorldMapScene.singleton.getPointManager().getCanBuildPointCount(blockPointIds);
        int currentCount = WorldMapScene.singleton.getPointManager().getMainPoints(blockPointIds, PointType.RALLY).size();

        canBuildPointCount = canBuildPointCount + (currentCount + PointType.RALLY.getArea());
        double percent = GameConstants.BLOCK_RALLY_PERCENT;
        int totalCount = (int) (canBuildPointCount * percent / PointType.RALLY.getArea());
        int refreshCount = Math.max(0, totalCount - currentCount);
        int realCount = 0;
        for (int i = 0; i < refreshCount; i++) {
            Integer rallyId = RandomUtil.randomOneKey(rate);
            if (rallyId == null) {
                break;
            }
            PointRallyData rallyData = new PointRallyData(rallyId);
            int mainPointId = WorldMapScene.singleton.putPoint(blockPointIds, rallyData);
            if (mainPointId == PointUtil.INVALID_POINT_ID) {
                break;
            } else {
                realCount++;
            }
        }
        if (refreshCount > 0 && realCount > 0) {
            log.info("add block rally,DayId={}blockBandId={},blockId={},total={},refresh={},real={}", mapRefreshDayId, blockBandId, blockId, totalCount, refreshCount, realCount);
        }
    }

    /**
     * 更新刷新信息
     *
     * @param mapRefreshDayId
     * @param blockBandId
     * @param blockId
     */
    private void updateWorldRallyBlockRefreshInfo(int mapRefreshDayId, int blockBandId, Integer blockId) {
        long intervalMillis = GameConstants.BLOCK_RALLY_REFRESH_MILLIS;
        long offsetMillis = blockId * (intervalMillis / WorldMapPointRegulation.getBlocks().size());
        long refreshTimestamp = ExDateTimeUtil.getPrevDayStart() - offsetMillis;
        while (refreshTimestamp <= System.currentTimeMillis()) {
            refreshTimestamp += intervalMillis;
        }
        blockRallyRefreshInfoMap.put(blockId, new WorldRallyBlockRefreshInfo(blockId, mapRefreshDayId, refreshTimestamp));
    }

    private Map<Integer, Integer> calcRallyRate(RallyRefresh rallyRefresh) {
        Map<Integer, Integer> rate = Maps.newHashMap(rallyRefresh.getRate());
        int maxRallyLevel = MapLimitLevelConfig.getInstance().getMaxRallyLevel(GameEngine.getInstance().getOpenedDays());
        List<SlgRallyInfo> slgRallyInfoList = SlgRallyInfoConfig.getInstance().getSpecList();
        for (SlgRallyInfo slgRallyInfo : slgRallyInfoList) {
            if (slgRallyInfo.getLevel() > maxRallyLevel) {
                rate.remove(slgRallyInfo.getId());
            }
        }
        return rate;
    }
}
