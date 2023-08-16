package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.ImmutableMap;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.MathUtil;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.PointRegulation;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.resource.GameConfigSupport;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WorldMapPointRegulation extends PointRegulation {
    private static final int MAP_MAX_X = 534;
    private static final int MAP_MAX_Y = 1066;
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

    static {
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

    private Map<Integer, WorldPoint> initWorldMapPoints() {
        Map<Integer, WorldPoint> initialWorldPoint = new HashMap<>();
        initMapFile(initialWorldPoint);
        initThroneCity(initialWorldPoint);
        return initialWorldPoint;
    }

    private void initThroneCity(Map<Integer, WorldPoint> initialWorldPoint) {

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
}
