package org.pot.game.engine.scene;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.persistence.entity.WorldPointEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
public class PointManager {

    @Getter
    private final AbstractScene scene;
    private final List<PointListener> listeners = new CopyOnWriteArrayList<>();

    private final Map<Integer, WorldPoint> worldPoints = new ConcurrentHashMap<>();

    public PointManager(AbstractScene scene) {
        this.scene = scene;
    }

    public void tick() {
        scene.getPointRegulation().tick();
    }

    protected void init() {
        worldPoints.putAll(scene.getPointRegulation().init());
        for (WorldPoint worldPoint : worldPoints.values()) {
            boolean illegal = scene.getPointRegulation().getPoint(worldPoint.getId()) != null;
            if (illegal) {
                log.error("{} point data is illegal", scene.getName());
            }
            if (!worldPoint.isMainPoint()) {
                continue;
            }
            PointExtraData rawExtraData = worldPoint.getRawExtraData();
            if (rawExtraData == null) {
                continue;
            }
            listeners.forEach(listeners -> listeners.onPointAdded(worldPoint));
        }

    }

    public void addListener(PointListener pointListener) {
        listeners.add(pointListener);
    }

    public long countPointType(PointType pointType) {
        return worldPoints.values().stream().filter(worldPoint -> worldPoint.isMainPoint() && pointType.equals(worldPoint.getType())).count();
    }

    public List<WorldPoint> getMainPoints(PointType pointType) {
        return worldPoints.values().stream().filter(point -> point.isMainPoint() && pointType.equals(point.getType())).collect(Collectors.toList());
    }

    public List<WorldPoint> getMainPoints(List<Integer> pointIds, PointType pointType) {
        List<WorldPoint> list = getPoints(pointIds);
        return list;
    }

    public Collection<WorldPoint> getPoints() {
        return Collections.unmodifiableCollection(worldPoints.values());
    }

    public List<WorldPoint> getPoints(List<Integer> pointIds) {
        List<WorldPoint> list = Lists.newArrayListWithExpectedSize(pointIds.size());
        for (Integer pointId : pointIds) {
            WorldPoint worldPoint = getPoint(pointId);
            if (worldPoint != null) {
                list.add(worldPoint);
            }
        }
        return list;
    }

    public WorldPoint getPoint(int pointId) {
        if (scene.getPointRegulation().isValidCoors(pointId)) {
            return null;
        }
        WorldPoint w;
        return (w = worldPoints.get(pointId)) != null ? w : scene.getPointRegulation().getPoint(pointId);
    }

    public WorldPoint getPoint(int x, int y) {
        return getPoint(PointUtil.getPointId(x, y));
    }

    public void removePoint(WorldPoint worldPoint) {
        removePoint(worldPoint.getId());
    }

    public void removePoint(int pointId) {
        scene.submit(() -> {
            WorldPoint worldPoint = worldPoints.get(pointId);
            if (worldPoint == null) {
                return;
            }
            PointType pointType = worldPoint.getType();
            if (pointType == null) {
                log.error("{} point type is null", scene.getName());
                worldPoints.values().removeIf(w -> w.getMainId() == worldPoint.getMainId());
                return;
            }
            if (pointType.isInitial()) {
                return;
            }
            int mainX = worldPoint.getMainX();
            int mainY = worldPoint.getMainY();
            int iRange = worldPoint.getType().getIRange();
            int jRange = worldPoint.getType().getJRange();
            List<Integer> occupiedPoints = PointRegulation.getOccupiedPoints(mainX, mainY, iRange, jRange);
            occupiedPoints.forEach(worldPoints::remove);
            scene.getPointRegulation().removePoint(occupiedPoints);
        });
    }

    public boolean isCanBuild(int pointId) {
        if (scene.getPointRegulation().isInvalidCoords(pointId)) return false;
        if (!scene.getPointRegulation().isCanBuild(pointId)) return false;
        WorldPoint worldPoint = getPoint(pointId);
        return worldPoint == null || worldPoint.getType() == PointType.LAND;
    }

    public boolean isCanBuild(int pointId, PointType pointType) {
        return isCanBuild(pointId, pointType.getIRange(), pointType.getJRange());
    }

    public boolean isCanBuild(int pointId, int iRange, int jRange) {
        return PointRegulation.getOccupiedPoints(pointId, iRange, jRange).stream().allMatch(this::isCanBuild);
    }

    public int getCanBuildPointCount(List<Integer> pointIds) {
        return (int) pointIds.stream().filter(this::isCanBuild).count();
    }

    public int allocateRandomLocation(Collection<Integer> pointIds, PointExtraData pointExtraData) {
        scene.requireThreadSafe();
        List<Integer> canBuildPointIds = CollectionUtil.shuffle(getCanBuildPoints(pointIds));
        PointType pointType = pointExtraData.getPointType();
        for (Integer mainPointId : canBuildPointIds) {
            int mainX = PointUtil.getPointX(mainPointId);
            int mainY = PointUtil.getPointY(mainPointId);
            List<Integer> occupiedPoints = PointRegulation.getOccupiedPoints(mainX, mainY, pointType);
            boolean match = occupiedPoints.stream().allMatch(pid -> pointIds.contains(pid) && isCanBuild(pid));
            if (match) {
                for (Integer pointId : occupiedPoints) {
                    int xCoordinate = PointUtil.getPointX(pointId);
                    int yCoordinate = PointUtil.getPointY(pointId);
                    WorldPointEntity worldPointEntity = WorldPointEntity.builder()
                            .id(pointId).type(pointType.getId())
                            .x(xCoordinate).y(yCoordinate)
                            .mainX(mainX).mainY(mainY)
                            .extraData(pointId.intValue() == mainPointId.intValue() ? pointExtraData : null)
                            .build();
                    addPoint(worldPointEntity);
                }
                return PointUtil.getPointId(mainX, mainY);
            }
        }
        return PointUtil.INVALID_POINT_ID;
    }

    public int allocateSpecifyLocation(int mainPointId, PointExtraData pointExtraData) {
        scene.requireThreadSafe();
        PointType pointType = pointExtraData.getPointType();
        int mainX = PointUtil.getPointX(mainPointId);
        int mainY = PointUtil.getPointY(mainPointId);
        List<Integer> occupiedPoints = PointRegulation.getOccupiedPoints(mainX, mainY, pointType);
        boolean match = occupiedPoints.stream().allMatch(this::isCanBuild);
        if (match) {
            for (Integer pointId : occupiedPoints) {
                int xCoordinate = PointUtil.getPointX(pointId);
                int yCoordinate = PointUtil.getPointY(pointId);
                WorldPointEntity worldPointEntity = WorldPointEntity.builder()
                        .id(pointId).type(pointType.getId())
                        .x(xCoordinate).y(yCoordinate)
                        .mainX(mainX).mainY(mainY)
                        .extraData(pointId == mainPointId ? pointExtraData : null)
                        .build();
                addPoint(worldPointEntity);
            }
            return PointUtil.getPointId(mainX, mainY);
        }
        return PointUtil.INVALID_POINT_ID;
    }

    private void addPoint(WorldPointEntity worldPointEntity) {
        scene.requireThreadSafe();
        WorldPoint worldPoint = new WorldPoint(scene, worldPointEntity);
        worldPoints.put(worldPoint.getId(), worldPoint);
        scene.getPointRegulation().addPoint(worldPointEntity);
        listeners.forEach(listeners -> listeners.onPointAdded(worldPoint));
    }

    public List<Integer> getCanBuildPoints(Collection<Integer> pointIds) {
        return pointIds.stream().filter(this::isCanBuild).collect(Collectors.toList());
    }

    public PointType getPointType(int pointId) {
        WorldPoint worldPoint = getPoint(pointId);
        return worldPoint == null ? PointType.NONE : worldPoint.getType();
    }

    public PointExtraData getPointExtraData(int pointId) {
        WorldPoint worldPoint = getPoint(pointId);
        return worldPoint == null ? null : worldPoint.getRawExtraData();
    }

    public void save(boolean async) {
        scene.getPointRegulation().save(async);
    }
}
