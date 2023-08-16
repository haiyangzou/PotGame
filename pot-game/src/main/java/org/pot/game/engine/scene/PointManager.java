package org.pot.game.engine.scene;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.pot.game.engine.enums.PointType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PointManager {

    @Getter
    private final AbstractScene scene;


    private final Map<Integer, WorldPoint> worldPoints = new ConcurrentHashMap<>();

    public PointManager(AbstractScene scene) {
        this.scene = scene;
    }

    protected void init() {
        worldPoints.putAll(scene.getPointRegulation().init());

    }

    public long countPointType(PointType pointType) {
        return worldPoints.values().stream().filter(worldPoint -> worldPoint.isMainPoint() && pointType.equals(worldPoint.getType())).count();
    }

    public List<WorldPoint> getMainPoints(List<Integer> pointIds, PointType pointType) {
        List<WorldPoint> list = getPoints(pointIds);
        return list;
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


}
