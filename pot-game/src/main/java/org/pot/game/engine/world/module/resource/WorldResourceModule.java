package org.pot.game.engine.world.module.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.*;
import org.pot.game.engine.GameConstants;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointResourceData;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;
import org.pot.game.engine.world.module.map.scene.WorldBand;
import org.pot.game.engine.world.module.map.scene.WorldBlock;
import org.pot.game.engine.world.module.map.scene.WorldMapPointRegulation;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;
import org.pot.game.engine.world.module.var.WorldVarDef;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.map.ResourceRefresh;
import org.pot.game.resource.map.ResourceRefreshConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class WorldResourceModule extends AbstractWorldModule {
    public static WorldResourceModule getInstance() {
        return WorldModuleType.WORLD_RESOURCE.getInstance();
    }

    private volatile long blackEarthRefreshTime = 0;
    private final Map<Integer, List<WorldResourceBlockRefreshInfo>> bandResourceRefreshInfoMap = new ConcurrentHashMap<>();
    private final RunSignal refreshSignal = new RunSignal(TimeUnit.SECONDS.toMillis(1));
    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));

    @Override
    public CompletableFuture<?> init() {
        loadResourceInfo();
        return null;
    }

    @Override
    public void tick() {
        if (refreshSignal.signal()) {
            refreshBandResource();
            refreshBlackEarthResource();
        }
        saveSignal.run(this::saveResourceInfo);
    }

    private void loadResourceInfo() {
        blackEarthRefreshTime = WorldVarDef.LongVar.BlackEarthResourceRefreshTime.value(0L);
        Map<Integer, List<WorldResourceBlockRefreshInfo>> map = WorldVarDef.StringVar.BandResourceRefreshInfo.parseJson(
                new TypeReference<Map<Integer, List<WorldResourceBlockRefreshInfo>>>() {
                }
        );
        if (map != null) {
            bandResourceRefreshInfoMap.putAll(map);
        }
    }

    private void saveResourceInfo() {
        WorldVarDef.LongVar.BlackEarthResourceRefreshTime.update(blackEarthRefreshTime);
        WorldVarDef.StringVar.BandResourceRefreshInfo.update(JsonUtil.toJson(bandResourceRefreshInfoMap));
    }

    private void refreshBlackEarthResource() {


    }

    private void refreshResource(String prefix, List<Integer> pointIds, double percent, Supplier<PointResourceData> resourceDataSupplier) {
        List<WorldPoint> currentResourcePoints = WorldMapScene.singleton.getPointManager().getMainPoints(pointIds, PointType.RESOURCE);
        currentResourcePoints.removeIf(worldPoint -> {
            PointResourceData resourceData = worldPoint.getExtraData(PointResourceData.class);
            return resourceData.getOccupier() > 0 || !resourceData.getMarchIdList().isEmpty();
        });
        //移除世界上的空閒的資源田
        currentResourcePoints.forEach(WorldMapScene.singleton::removePoint);
        //獲取坐標中可建造的做標書
        int canBuildPointCount = WorldMapScene.singleton.getPointManager().getCanBuildPointCount(pointIds);
        int currentCount = WorldMapScene.singleton.getPointManager().getMainPoints(pointIds, PointType.RESOURCE).size();
        canBuildPointCount = canBuildPointCount + (currentCount + PointType.RESOURCE.getArea());
        int totalCount = (int) (canBuildPointCount * percent / PointType.RESOURCE.getArea());
        int refreshCount = Math.max(0, totalCount - currentCount);
        int realCount = 0;
        for (int i = 0; i < refreshCount; i++) {
            PointResourceData resourceData = resourceDataSupplier.get();
            int mainPointId = WorldMapScene.singleton.putPoint(pointIds, resourceData);
            if (mainPointId == PointUtil.INVALID_POINT_ID) {
                break;
            } else {
                realCount++;
            }
        }
        if (refreshCount > 0 && realCount > 0) {
            log.info("{},total={},refresh={},real={}", prefix, totalCount, refreshCount, realCount);
        }
    }

    private void refreshBandResource() {
        boolean changed = false;
        for (Integer blockBandId : WorldMapPointRegulation.getHighToLowResourceBandIdList()) {
            changed = refreshBandResource(blockBandId) || changed;
        }
    }

    private boolean refreshBandResource(int blockBandId) {
        List<WorldResourceBlockRefreshInfo> refresh = bandResourceRefreshInfoMap.get(blockBandId);
        if (refresh == null) {
            WorldBand band = WorldMapPointRegulation.getBlockBand(blockBandId);
            band.getBlockIds().forEach(blockId -> refreshBlockResource(blockBandId, blockId));
            return refreshBandSequence(blockBandId);
        }
        return false;
    }

    private void refreshBlockResource(int blockBandId, Integer blockId) {
        WorldBlock worldBlock = WorldMapPointRegulation.getBlock(blockId);
        int resourceBandId = WorldMapPointRegulation.mapBandId4Block2Resource(blockBandId);
        String prefix = StringUtil.format("refresh band resource,band={},block={}", blockBandId, blockId);
        List<Integer> blockPointsIds = worldBlock.getNotInBlackEarthPointIds();
        Double percent = GameConstants.BAND_RESOURCE_PERCENTS.get(resourceBandId);
        refreshResource(prefix, blockPointsIds, percent, () -> {
            ResourceRefreshConfig config = GameConfigSupport.getConfig(ResourceRefreshConfig.class);
            ResourceRefresh refresh = config.getSpec(blockBandId);
            Integer resourceId = RandomUtil.randomOneKey(refresh.getTile());
            return new PointResourceData(resourceId);
        });
    }

    private boolean refreshBandSequence(int blockBandId) {
        if (CollectionUtil.isNotEmpty(bandResourceRefreshInfoMap.get(blockBandId))) {
            return false;
        }
        WorldBand band = WorldMapPointRegulation.getBlockBand(blockBandId);
        List<Integer> randomBlockIds = CollectionUtil.copyAndShuffle(band.getBlockIds());
        List<WorldResourceBlockRefreshInfo> infos = Lists.newArrayListWithExpectedSize(randomBlockIds.size());
        long intervalMillis = GameConstants.BAND_RESOURCE_REFRESH_MILLIS;
        long perMillis = intervalMillis / randomBlockIds.size();
        long timestamp = System.currentTimeMillis();
        for (int i = 0; i < randomBlockIds.size(); i++) {
            int blockId = randomBlockIds.get(i);
            long refreshTimestamp = timestamp + ((i + 1) * perMillis);
            infos.add(new WorldResourceBlockRefreshInfo(blockId, refreshTimestamp));
        }
        bandResourceRefreshInfoMap.put(blockBandId, infos);
        return false;
    }
}
