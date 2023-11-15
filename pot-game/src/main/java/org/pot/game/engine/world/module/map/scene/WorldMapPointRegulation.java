package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.*;
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.point.PointThroneData;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.PointManager;
import org.pot.game.engine.scene.PointRegulation;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.module.map.born.PlayerBornRule;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.WorldPointEntity;
import org.pot.game.persistence.mapper.WorldPointEntityMapper;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.map.WorldMapDecoration;
import org.pot.game.resource.map.WorldMapDecorationConfig;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private static volatile List<Integer> BlackEarth;
    @Getter
    private static volatile int NorthBlackEarthVertex;
    @Getter
    private static volatile int SouthBlackEarthVertex;
    @Getter
    private static volatile int WestBlackEarthVertex;
    @Getter
    private static volatile int EastBlackEarthVertex;
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

        List<Integer> temBlackEarth = PointRegulation.getOccupiedPoints(MAP_CENTER_X, MAP_CENTER_Y, BLACK_RANGE, BLACK_RANGE);
        temBlackEarth.remove(Throne);
        temBlackEarth.remove(AllTower);
        temBlackEarth.remove(ThroneCity);
        BlackEarth = ImmutableList.copyOf(temBlackEarth);
        NorthBlackEarthVertex = getBlackEarthVertex(Direction.NORTH);
        SouthBlackEarthVertex = getBlackEarthVertex(Direction.SOUTH);
        WestBlackEarthVertex = getBlackEarthVertex(Direction.WEST);
        EastBlackEarthVertex = getBlackEarthVertex(Direction.EAST);
        List<Integer> tempLowToHighBlockBand = new ArrayList<>();
        List<Integer> tempHighToLowBlockBand = new ArrayList<>();
        Map<Integer, WorldBand> tempWorldBlockBandMap = new TreeMap<>(Integer::compareTo);
        for (int i = 0; i < BLOCK_BAND_COUNT; i++) {
            int bandId = i + 1;
            tempLowToHighBlockBand.add(bandId);
            tempHighToLowBlockBand.add(0, bandId);
            tempWorldBlockBandMap.put(bandId, new WorldBand(bandId));
        }
        lowToHighBlockIdList = ImmutableList.copyOf(tempLowToHighBlockBand);
        highToLowBlockBandIdList = ImmutableList.copyOf(tempHighToLowBlockBand);
        BlockBandMap = ImmutableMap.copyOf(tempWorldBlockBandMap);

        List<Integer> tempLowToHighResourceBand = new ArrayList<>();
        List<Integer> tempHighToLowResourceBand = new ArrayList<>();
        Map<Integer, WorldBand> tempWorldResourceBandMap = new TreeMap<>(Integer::compareTo);
        for (int i = 0; i < RESOURCE_BAND_COUNT; i++) {
            int bandId = i + 1;
            tempLowToHighResourceBand.add(bandId);
            tempHighToLowResourceBand.add(0, bandId);
            tempWorldResourceBandMap.put(bandId, new WorldBand(bandId));
        }
        lowToHighResourceBandIdList = ImmutableList.copyOf(tempLowToHighResourceBand);
        highToLowResourceBandIdList = ImmutableList.copyOf(tempHighToLowResourceBand);
        ResourceBandMap = ImmutableMap.copyOf(tempWorldResourceBandMap);

        Map<Integer, WorldBlock> tempWorldBlockMap = new HashMap<>();
        int blockId = 0;
        for (int i = 0; i < BLOCK_COUNT; i++) {
            for (int j = 0; j < BLOCK_COUNT; j++) {
                WorldBlock worldBlock = new WorldBlock(++blockId, i * BLOCK_LENGTH_X, j * BLOCK_LENGTH_Y);
                tempWorldBlockMap.put(blockId, worldBlock);
                addToBand(worldBlock, BlockBandMap, 1);
                addToBand(worldBlock, ResourceBandMap, RESOURCE_BAND_WIDTH);
            }
        }
        BlockMap = ImmutableMap.copyOf(tempWorldBlockMap);
    }

    private static void addToBand(WorldBlock worldBlock, Map<Integer, WorldBand> bandMap, int bandBlockWith) {
        int i = worldBlock.getX() / BLOCK_LENGTH_X;
        int j = worldBlock.getX() / BLOCK_LENGTH_X;
        for (WorldBand band : bandMap.values()) {
            int bandId = band.getId();
            int bandIndex = bandId - 1;
            int bandBlockHeadMin = bandIndex * bandBlockWith;
            int bandBlockHeadMax = bandId * bandBlockWith;
            int bandBlockTaiMin = BLOCK_COUNT - bandBlockHeadMax;
            int bandBlockTaiMax = BLOCK_COUNT - bandBlockHeadMin;
            if ((i >= bandBlockHeadMin && i < bandBlockHeadMax) || (i >= bandBlockTaiMin && i < bandBlockTaiMax)) {
                band.addBlockId(worldBlock.getId());
                break;
            }
            if ((j >= bandBlockHeadMin && j < bandBlockHeadMax) || (j >= bandBlockTaiMin && j < bandBlockTaiMax)) {
                band.addBlockId(worldBlock.getId());
                break;
            }
        }
    }

    private static Integer getBlackEarthVertex(Direction direction) {
        int centerI = (MAP_CENTER_X - MAP_CENTER_Y) / 2;
        int centerJ = (MAP_CENTER_X + MAP_CENTER_Y) / 2;
        int blackEarthOffset = BLACK_RANGE / 2;
        int iCoordinate = direction.iMove(centerI, blackEarthOffset);
        int jCoordinate = direction.jMove(centerJ, blackEarthOffset);
        int xCoordinate = jCoordinate + iCoordinate;
        int yCoordinate = jCoordinate - iCoordinate;
        return PointUtil.getPointId(xCoordinate, yCoordinate);
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

    @Override
    public WorldMapScene getScene() {
        return (WorldMapScene) scene;
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

    private final ImmutableList<RunSignal> validateSignals = ImmutableList.of(
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)));

    @Override
    protected Map<Integer, WorldPoint> init() {
        initialWorldPoint = ImmutableMap.copyOf(initWorldMapPoints());
        SqlSession sqlSession = GameDb.local().getSqlSession(WorldPointEntityMapper.class);
        WorldPointEntityMapper worldPointEntityMapper = sqlSession.getMapper(WorldPointEntityMapper.class);
        return worldPointEntityMapper.all().stream()
                .map(e -> new WorldPoint(scene, e.getType(), e.getX(), e.getY(), e.getMainX(), e.getMainY(), e.getExtraData()))
                .collect(Collectors.toMap(WorldPoint::getId, p -> p));
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

    @Override
    protected void onAddPoint(WorldPoint worldPoint) {
        SqlSession sqlSession = GameDb.local().getSqlSession(WorldPointEntityMapper.class);
        sqlSession.submitWithoutResult(WorldPointEntityMapper.class, m -> m.insertOnDuplicateKeyUpdate(toWorldPointEntity(worldPoint)));
    }

    private WorldPointEntity toWorldPointEntity(WorldPoint worldPoint) {
        return WorldPointEntity.builder()
                .id(worldPoint.getId())
                .type(worldPoint.getType().getId())
                .x(worldPoint.getX())
                .y(worldPoint.getY())
                .mainX(worldPoint.getMainX())
                .mainY(worldPoint.getMainY())
                .extraData(worldPoint.getExtraData())
                .build();
    }

    @Override
    protected void save(boolean async) {
        PlayerBornRule.getInstance().save();
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

    @Override
    protected void tick() {
        for (int i = 0; i < validateSignals.size() - 1; i++) {
            if (validateSignals.get(i).signal()) validatePoint(i);
        }
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

    @Override
    protected void onRemovePoint(PointType pointType, int mainX, int mainY, List<Integer> pointIds) {
        SqlSession sqlSession = GameDb.local().getSqlSession(WorldPointEntityMapper.class);
        sqlSession.submitWithoutResult(WorldPointEntityMapper.class, m -> m.deleteInIdList(pointIds));
    }

    public static double getInBlackEarthDistance(int startPoint, int endPoint) {
        return 0;
    }

    @Override
    public double inBlackEarthDistance(int startPoint, int endPoint) {
        if (WorldBuilding.TempleBattleBuildingPoints.contains(startPoint) || WorldBuilding.TempleBattleBuildingMainPoints.contains(endPoint)) {
            return getInBlackEarthDistance(startPoint, endPoint);
        }
        return 0;
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

    @Data
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
