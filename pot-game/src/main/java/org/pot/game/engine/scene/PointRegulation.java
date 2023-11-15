package org.pot.game.engine.scene;

import com.google.common.collect.Lists;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.enums.PointType;
import org.pot.game.persistence.entity.WorldPointEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class PointRegulation {
    protected final AbstractScene scene;

    public PointRegulation(AbstractScene scene) {
        this.scene = scene;
    }

    public boolean isInvalidCoords(int pointId) {
        return !isValidCoordinate(PointUtil.getPointX(pointId), PointUtil.getPointY(pointId));
    }

    public boolean isValidCoordinate(int x, int y) {
        if (x <= 0 || y <= 0) {
            return false;
        }
        if (x >= getMaxX() || y >= getMaxY()) {
            return false;
        }
        return (x + y) % 2 == 0;
    }

    public boolean isValidCoors(int pointId) {
        return !isValidCoordinate(PointUtil.getPointX(pointId), PointUtil.getPointX(pointId));
    }

    public abstract int getMaxX();

    public abstract int getMaxY();

    public abstract WorldPoint getPoint(int pointId);

    protected abstract Map<Integer, WorldPoint> init();

    public static List<Integer> getOccupiedPoints(int mainPointId, int iRange, int jRange) {
        return getOccupiedPoints(PointUtil.getPointX(mainPointId), PointUtil.getPointY(mainPointId), iRange, jRange);
    }

    public static List<Integer> getOccupiedPoints(int mainX, int mainY, PointType pointType) {
        return getOccupiedPoints(mainX, mainY, pointType.getIRange(), pointType.getJRange());
    }

    public static List<Integer> getOccupiedPoints(int mainX, int mainY, int iRange, int jRange) {
        //x和y是主坐标,iRange和jRange是占的各自数量
        //利用主坐标和占格数量，将所有格子填满
        int mainI = (mainX - mainY) / 2;
        int mainJ = (mainX + mainY) / 2;
        int iOffset = iRange / 2;
        int jOffset = (jRange - 1) / 2;
        int iStart = mainI - iOffset;
        int jStart = mainJ - jOffset;
        List<Integer> list = Lists.newArrayListWithExpectedSize(iRange * jRange);
        for (int iStep = 0; iStep < iRange; iStep++) {
            for (int jStep = 0; jStep < jRange; jStep++) {
                int iCoordinate = iStart + iStep;
                int jCoordinate = jStart + jStep;
                int xCoordinate = jCoordinate + iCoordinate;
                int yCoordinate = jCoordinate - iCoordinate;
                list.add(PointUtil.getPointId(xCoordinate, yCoordinate));
            }
        }
        return list;
    }

    public static int getMainId(Collection<Integer> pointIds, int iRange, int jRange) {
        for (Integer pointId : pointIds) {
            List<Integer> temp = getOccupiedPoints(pointId, iRange, jRange);
        }
        return PointUtil.INVALID_POINT_ID;
    }


    protected abstract void addPoint(WorldPointEntity add);

    protected abstract void removePoint(List<Integer> remove);

    public abstract boolean isCanBuild(int pointId);

    protected abstract void onAddPoint(WorldPoint worldPoint);

    protected abstract void save(boolean async);

    protected abstract void tick();

    protected abstract void onRemovePoint(PointType pointType, int mainX, int mainY, List<Integer> pointIds);

    public abstract double inBlackEarthDistance(int startPoint, int endPoint);

    public abstract AbstractScene getScene();

}
