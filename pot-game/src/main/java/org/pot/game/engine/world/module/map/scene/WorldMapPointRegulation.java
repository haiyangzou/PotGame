package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.MathUtil;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.point.PointThroneData;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.PointRegulation;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.persistence.entity.WorldPointEntity;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.map.WorldMapDecoration;
import org.pot.game.resource.map.WorldMapDecorationConfig;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class WorldMapPointRegulation extends PointRegulation {
    //
    private static final int MAP_MAX_X = 534;
    private static final int MAP_MAX_Y = 1066;
    //地图中央坐标
    private static final int MAP_CENTER_X = MAP_MAX_X / 2;
    private static final int MAP_CENTER_Y = MAP_MAX_Y / 2;
    //大地图的资源带的宽度包含几个Block带
    private static final int RESOURCE_BAND_WIDTH = 2;
    //大地图的Block数量，横竖一样多
    private static final int BLOCK_COUNT = 28;

    private static final int BLOCK_BAND_COUNT = BLOCK_COUNT / 2;
    //每个Block的尺寸
    protected static final int BLOCK_LENGTH_X = MAP_MAX_X / BLOCK_COUNT;
    protected static final int BLOCK_LENGTH_Y = MAP_MAX_Y / BLOCK_COUNT;
    //大地图的资源带数量
    private static final int RESOURCE_BAND_COUNT = BLOCK_BAND_COUNT / RESOURCE_BAND_WIDTH;
    //黑土地所占个数
    private static final int BLACK_RANGE = 41;
    //哨塔
    private static final int TOWER_RANGE = 2;
    //王座
    private static final int THRONE_RANGE = 5;
    //王城区
    private static final int THRONE_CITY_RANGE = 13;

    //地编
    private static final String CE_FILE_NAME = "Map_NormalLand.json";
    private static final String CE_BLACK_FILE_NAME = "Map_BlackLand.json";
    //地编块尺寸
    private static final int CE_BLOCK_LENGTH_X = 240;
    private static final int CE_BLOCK_LENGTH_Y = 240;
    //起点偏移
    private static final int CE_BLOCK_OFFSET_X = (MAP_MAX_X % CE_BLOCK_LENGTH_X) / 2;
    private static final int CE_BLOCK_OFFSET_Y = (MAP_MAX_Y % CE_BLOCK_LENGTH_Y) / 2;
    //铺满数量
    private static final int CE_BLOCK_COUNT_X = MathUtil.divideAndCeil(MAP_MAX_X, CE_BLOCK_LENGTH_X);
    private static final int CE_BLOCK_COUNT_Y = MathUtil.divideAndCeil(MAP_MAX_X, CE_BLOCK_LENGTH_X);

    private static volatile Map<Integer, WorldBand> ResourceBandMap; //资源带,由N圈Block带组成，N>=1
    private static volatile Map<Integer, WorldBlock> BlockMap; //世界地图的Block定义

    @Getter
    private static volatile List<Integer> Throne;
    @Getter
    public static volatile int ThroneMainId;
    @Getter
    private static volatile List<Integer> NorthTower;
    @Getter
    public static volatile int NorthTowerMainId;
    @Getter
    private static volatile List<Integer> SouthTower;
    @Getter
    public static volatile int SouthTowerMainId;
    @Getter
    private static volatile List<Integer> WestTower;
    @Getter
    public static volatile int WestTowerMainId;
    @Getter
    private static volatile List<Integer> EastTower;
    @Getter
    public static volatile int EastTowerMainId;
    @Getter
    private static volatile List<Integer> ThroneCity;
    @Getter
    private static volatile List<Integer> lowToHighBlockIdList;//block带定义
    @Getter
    private static volatile List<Integer> highToLowBlockBandIdList;
    @Getter
    private static volatile List<Integer> lowToHighResourceBandIdList;//资源带定义
    @Getter
    private static volatile List<Integer> highToLowResourceBandIdList;
    @Getter
    private static volatile List<Integer> AllTower;
    @Getter
    private static volatile Map<Integer, WorldBand> BlockBandMap;

    static {
        NorthTower = ImmutableList.copyOf(getTowerOccupiedPoints(Direction.NORTH));
        NorthTowerMainId = PointRegulation.getMainId(NorthTower, TOWER_RANGE, TOWER_RANGE);
        SouthTower = ImmutableList.copyOf(getTowerOccupiedPoints(Direction.SOUTH));
        SouthTowerMainId = PointRegulation.getMainId(SouthTower, TOWER_RANGE, TOWER_RANGE);
        WestTower = ImmutableList.copyOf(getTowerOccupiedPoints(Direction.WEST));
        WestTowerMainId = PointRegulation.getMainId(WestTower, TOWER_RANGE, TOWER_RANGE);
        EastTower = ImmutableList.copyOf(getTowerOccupiedPoints(Direction.EAST));
        EastTowerMainId = PointRegulation.getMainId(EastTower, TOWER_RANGE, TOWER_RANGE);
        AllTower = new ImmutableList.Builder<Integer>().addAll(NorthTower).addAll(SouthTower).addAll(WestTower).addAll(EastTower).build();
        Throne = ImmutableList.copyOf(PointRegulation.getOccupiedPoints(MAP_CENTER_X, MAP_CENTER_Y, THRONE_RANGE, THRONE_RANGE));
        ThroneMainId = PointUtil.getPointId(MAP_CENTER_X, MAP_CENTER_Y);

        List<Integer> tempThroneCity = PointRegulation.getOccupiedPoints(MAP_CENTER_X, MAP_CENTER_Y, THRONE_RANGE, THRONE_RANGE);
        tempThroneCity.removeAll(Throne);
        tempThroneCity.removeAll(AllTower);
        ThroneCity = ImmutableList.copyOf(tempThroneCity);
        Map<Integer, WorldBand> tempWorldResourceBandMap = new TreeMap<>(Integer::compareTo);
        for (int i = 0; i < RESOURCE_BAND_COUNT; i++) {
            int bandId = i + 1;
            tempWorldResourceBandMap.put(bandId, new WorldBand(bandId));
        }
        ResourceBandMap = ImmutableMap.copyOf(tempWorldResourceBandMap);

        Map<Integer, WorldBlock> tempWorldBlockMap = new HashMap<>();
        int blockId = 0;
        for (int i = 0; i < BLOCK_COUNT; i++) {
            for (int j = 0; j < BLOCK_COUNT; j++) {
                WorldBlock worldBlock = new WorldBlock(++blockId, i * BLOCK_LENGTH_X, j * BLOCK_LENGTH_Y);
                tempWorldBlockMap.put(blockId, worldBlock);
            }
        }
        BlockMap = ImmutableMap.copyOf(tempWorldBlockMap);
        List<Integer> tempLowToHighBlockBand = new ArrayList<>();
        List<Integer> tempHighToLowBlockBand = new ArrayList<>();
        Map<Integer, WorldBand> tempWorldBlockBandMap = new TreeMap<>(Integer::compareTo);
        for (int i = 0; i < BLOCK_BAND_COUNT; i++) {
            int bandId = i + 1;
            tempLowToHighBlockBand.add(bandId);
            tempHighToLowBlockBand.add(0, bandId);
            tempWorldBlockBandMap.put(bandId, new WorldBand(bandId));
        }
        BlockBandMap = ImmutableMap.copyOf(tempWorldBlockBandMap);

    }

    public static WorldBand getResourceBand(int resourceBandId) {
        return ResourceBandMap.get(resourceBandId);
    }

    public static WorldBlock getBlock(int blockId) {
        return BlockMap.get(blockId);
    }

    public WorldMapPointRegulation(AbstractScene scene) {
        super(scene);
    }

    private volatile Map<Integer, WorldPoint> initialWorldPoint = null;

    @Override
    public int getMaxX() {
        return MAP_MAX_X;
    }

    @Override
    public int getMaxY() {
        return MAP_MAX_Y;
    }

    @Override
    public WorldPoint getPoint(int pointId) {
        return initialWorldPoint == null ? null : initialWorldPoint.get(pointId);
    }

    @Override
    protected Map<Integer, WorldPoint> init() {
        initialWorldPoint = ImmutableMap.copyOf(initWorldMapPoints());
        return null;
    }

    @Override
    protected void addPoint(WorldPointEntity add) {

    }

    @Override
    protected void removePoint(List<Integer> remove) {

    }

    @Override
    public boolean isCanBuild(int pointId) {
        return false;
    }

    public static WorldBand getBlockBand(int blockBandId) {
        return BlockBandMap.get(blockBandId);
    }

    private Map<Integer, WorldPoint> initWorldMapPoints() {
        Map<Integer, WorldPoint> initialWorldPoint = new HashMap<>();
        initMapFile(initialWorldPoint);
        initThroneCity(initialWorldPoint);
        return initialWorldPoint;
    }

    private void initThroneCity(Map<Integer, WorldPoint> initialWorldPoint) {
        List<Integer> pointIds = getOccupiedPoints(MAP_CENTER_X, MAP_CENTER_Y, THRONE_CITY_RANGE, THRONE_CITY_RANGE);
        for (Integer pointId : pointIds) {
            WorldPoint worldPoint = initialWorldPoint.get(pointId);
            if (worldPoint != null) {
                int mainId = worldPoint.getMainId();
                //望城区上存在地编物体，删除掉，并且告警
                initialWorldPoint.values().removeIf(p -> {
                    if (p.getMainId() == mainId) {
                        log.error("{} remove throne city decoration({},{})", scene.getName(), p.getX(), p.getY());
                        return true;
                    }
                    return false;
                });
            }
            if (isThrone(pointId)) {
                worldPoint = getThroneCityPoint(pointId, getThroneMainId(), PointType.THRONE, () -> new PointThroneData(WorldBuilding.Throne));
            } else if (isNorthTower(pointId)) {
                worldPoint = getThroneCityPoint(pointId, getThroneMainId(), PointType.THRONE, () -> new PointThroneData(WorldBuilding.Throne));
            } else if (isSouthTower(pointId)) {
                worldPoint = getThroneCityPoint(pointId, getThroneMainId(), PointType.THRONE, () -> new PointThroneData(WorldBuilding.Throne));
            } else if (isWestTower(pointId)) {
                worldPoint = getThroneCityPoint(pointId, getThroneMainId(), PointType.THRONE, () -> new PointThroneData(WorldBuilding.Throne));
            } else if (isEastTower(pointId)) {
                worldPoint = getThroneCityPoint(pointId, getThroneMainId(), PointType.THRONE, () -> new PointThroneData(WorldBuilding.Throne));
            } else if (isThroneCity(pointId)) {
                worldPoint = getThroneCityPoint(pointId, getThroneMainId(), PointType.THRONE, () -> new PointThroneData(WorldBuilding.Throne));
            }
            initialWorldPoint.put(pointId, worldPoint);
        }

    }

    private void initMapFile(Map<Integer, WorldPoint> initialWorldPoint) {
        File normalFile = new File(GameConfigSupport.getBaseDirPath() + CE_FILE_NAME);
        TempForLoadPoint[] normalPoints = JsonUtil.parseJson(normalFile, TempForLoadPoint[].class);
        File blackFile = new File(GameConfigSupport.getBaseDirPath() + CE_BLACK_FILE_NAME);
        TempForLoadPoint[] blackPoints = JsonUtil.parseJson(blackFile, TempForLoadPoint[].class);
        for (int i = 0; i < CE_BLOCK_COUNT_X; i++) {
            for (int j = 0; j < CE_BLOCK_COUNT_Y; j++) {
                int offsetX = CE_BLOCK_OFFSET_X + (i * CE_BLOCK_LENGTH_X);
                int offsetY = CE_BLOCK_OFFSET_Y + (i * CE_BLOCK_LENGTH_Y);
                if (i == CE_BLOCK_COUNT_X / 2 && j == CE_BLOCK_COUNT_Y / 2) {
                    setWorldDecoration(blackPoints, initialWorldPoint, offsetX, offsetY);
                } else {
                    setWorldDecoration(normalPoints, initialWorldPoint, offsetX, offsetY);
                }
            }
        }
    }

    private void setWorldDecoration(TempForLoadPoint[] clientPoints, Map<Integer, WorldPoint> initialWorldPoint, int offsetX, int offsetY) {
        WorldMapDecorationConfig worldMapDecorationConfig = GameConfigSupport.getConfig(WorldMapDecorationConfig.class);
        for (TempForLoadPoint clientPoint : clientPoints) {
            int x = clientPoint.x + offsetX;
            int y = clientPoint.y + offsetY;
            WorldMapDecoration worldMapDecoration = worldMapDecorationConfig.getSpec(clientPoint.id);
            if (worldMapDecoration == null) {
                log.error("{} decoration not found.id={}", scene.getName(), clientPoint.id);
                continue;
            }
            if (worldMapDecoration.isCanBuild()) {
                continue;
            }
            expandDecoration(initialWorldPoint, x, y, worldMapDecoration.getIRange(), worldMapDecoration.getJRange());
        }
    }

    private void expandDecoration(Map<Integer, WorldPoint> initialWorldPoint, int mainX, int mainY, int iRange, int jRange) {
        List<Integer> occupiedPoints = getOccupiedPoints(mainX, mainY, iRange, jRange);
        for (Integer pointId : occupiedPoints) {
            int xCoordinate = PointUtil.getPointX(pointId);
            int yCoordinate = PointUtil.getPointY(pointId);
            WorldPoint worldPoint = new WorldPoint(scene, WorldPointEntity.builder().id(pointId).type(PointType.WONDER.getId()).x(xCoordinate).y(yCoordinate).mainX(mainX).mainY(mainY).build());
            WorldPoint oldWorldPoint = initialWorldPoint.put(pointId, worldPoint);
            if (oldWorldPoint != null) {
                log.error("{} point conflict ({},{}). range=({},{}),newMain=({},{}),oldMain=({},{})", scene.getName(), xCoordinate, yCoordinate, iRange, jRange, mainX, mainY, oldWorldPoint.getMainX(), oldWorldPoint.getMainY());
            }
        }
    }

    private static final class TempForLoadPoint {
        private int id, x, y;
    }

    public static List<Integer> getDisorderlyBlockIds() {
        return CollectionUtil.shuffle(getBlocks().stream().map(WorldBlock::getId).collect(Collectors.toList()));
    }

    public static Collection<WorldBlock> getBlocks() {
        return BlockMap.values();
    }

    public static boolean isThrone(int pointId) {
        return Throne.contains(pointId);
    }

    public static boolean isNorthTower(int pointId) {
        return NorthTower.contains(pointId);
    }

    public static boolean isSouthTower(int pointId) {
        return NorthTower.contains(pointId);
    }

    public static boolean isEastTower(int pointId) {
        return EastTower.contains(pointId);
    }

    public static boolean isWestTower(int pointId) {
        return WestTower.contains(pointId);
    }

    public static boolean isThroneCity(int pointId) {
        return ThroneCity.contains(pointId);
    }

    private WorldPoint getThroneCityPoint(int pid, int mid, PointType pointType, Supplier<PointExtraData> supplier) {
        return new WorldPoint(scene, WorldPointEntity.builder().id(pid).type(pointType.getId()).x(PointUtil.getPointX(pid)).y(PointUtil.getPointY(pid)).mainY(PointUtil.getPointY(mid)).mainX(PointUtil.getPointX(mid)).extraData(pid == mid && supplier != null ? supplier.get() : null).build());
    }

    private static List<Integer> getTowerOccupiedPoints(Direction direction) {
        int centerI = (MAP_CENTER_X - MAP_CENTER_Y) / 2;
        int centerJ = (MAP_CENTER_X + MAP_CENTER_Y) / 2;
        int towerOffset = THRONE_CITY_RANGE / 2;
        if (TOWER_RANGE != 2) {
            throw new IllegalStateException("illegal tower range");
        }
        int[][] towerScopeOffsetArray = new int[][]{
                {towerOffset, towerOffset},
                {towerOffset, towerOffset - 1},
                {towerOffset - 1, towerOffset},
                {towerOffset - 1, towerOffset - 1}
        };
        List<Integer> list = Lists.newArrayListWithExpectedSize(towerScopeOffsetArray.length);
        for (int[] offset : towerScopeOffsetArray) {
            int iCoordinate = direction.iMove(centerI, offset[0]);
            int jCoordinate = direction.jMove(centerJ, offset[1]);
            int xCoordinate = jCoordinate + iCoordinate;
            int yCoordinate = jCoordinate - iCoordinate;
            list.add(PointUtil.getPointId(xCoordinate, yCoordinate));
        }
        return list;
    }

    public static int mapBandId4Block2Resource(int blockBandId) {
        return MathUtil.divideAndCeil(blockBandId, RESOURCE_BAND_WIDTH);
    }
}
