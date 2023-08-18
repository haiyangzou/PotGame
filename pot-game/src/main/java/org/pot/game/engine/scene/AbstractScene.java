package org.pot.game.engine.scene;

import lombok.Getter;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.point.PointExtraData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractScene {
    @Getter
    protected final String name;
    @Getter
    protected final MarchManager marchManager;
    @Getter
    protected final PointManager pointManager;
    @Getter
    protected final PointRegulation pointRegulation;
    @Getter
    protected final CityRegulation cityRegulation;

    public AbstractScene(String name, Function<AbstractScene, CityRegulation> cityRegulation, Function<AbstractScene, PointRegulation> pointRegulation) {
        this.name = name;
        this.marchManager = new MarchManager(this);
        this.pointManager = new PointManager(this);
        this.pointRegulation = pointRegulation.apply(this);
        this.cityRegulation = cityRegulation.apply(this);
    }

    public void init() {
    }

    public WorldPoint getPoint(int x, int y) {
        return pointManager.getPoint(x, y);
    }

    public void removePoint(WorldPoint worldPoint) {
        pointManager.removePoint(worldPoint);
    }

    public abstract void requireThreadSafe();

    public abstract void submit(Runnable runnable);

    public abstract <T> CompletableFuture<T> submit(Supplier<T> supplier);

    public abstract <T> T execute(Supplier<T> supplier);

    public int putPoint(List<Integer> pointIds, PointExtraData pointExtraData) {
        return pointManager.allocateRandomLocation(pointIds, pointExtraData);
    }

    public int putPoint(Integer mainPointId, PointExtraData pointExtraData) {
        return pointManager.allocateSpecifyLocation(mainPointId, pointExtraData);
    }
}
