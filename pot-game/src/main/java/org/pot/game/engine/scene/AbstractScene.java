package org.pot.game.engine.scene;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.point.PointExtraData;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractScene {
    @Getter
    protected final String name;
    @Getter
    protected final ViewManger viewManger;
    @Getter
    protected final MarchManager marchManager;
    @Getter
    protected final PointManager pointManager;
    @Getter
    protected final CityManager cityManager;

    protected final PointRegulation pointRegulation;
    protected final CityRegulation cityRegulation;
    protected final MarchRegulation marchRegulation;

    public AbstractScene(String name,
                         Function<AbstractScene, CityRegulation> cityRegulation,
                         Function<AbstractScene, MarchRegulation> marchRegulation,
                         Function<AbstractScene, PointRegulation> pointRegulation) {
        this.name = name;
        this.viewManger = new ViewManger(this);
        this.cityManager = new CityManager(this);
        this.pointManager = new PointManager(this);
        this.marchManager = new MarchManager(this);
        this.cityRegulation = cityRegulation.apply(this);
        this.marchRegulation = marchRegulation.apply(this);
        this.pointRegulation = pointRegulation.apply(this);
        log.info("init scene");
    }

    public void init() {
        this.pointManager.init();
        this.marchManager.init();
        this.cityManager.init();
        this.viewManger.init();
    }

    public WorldPoint getPoint(int x, int y) {
        return pointManager.getPoint(x, y);
    }

    public WorldPoint getPoint(int pointId) {
        return pointManager.getPoint(pointId);
    }

    public void removePoint(WorldPoint worldPoint) {
        pointManager.removePoint(worldPoint);
    }

    public void removePoint(int pointId) {
        pointManager.removePoint(pointId);
    }

    public abstract void requireThreadSafe();

    public abstract void submit(Runnable runnable);

    public abstract <T> CompletableFuture<T> submit(Supplier<T> supplier);

    public abstract <T> T execute(Supplier<T> supplier);

    public int putPoint(Collection<Integer> pointIds, PointExtraData pointExtraData) {
        return pointManager.allocateRandomLocation(pointIds, pointExtraData);
    }

    public int putPoint(Integer mainPointId, PointExtraData pointExtraData) {
        return pointManager.allocateSpecifyLocation(mainPointId, pointExtraData);
    }

    public abstract CityRegulation getCityRegulation();

    public abstract MarchRegulation getMarchRegulation();

    public abstract boolean isThreadSafe();

    public abstract PointRegulation getPointRegulation();

}
