package org.pot.game.engine.world.module.map.scene;

import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.world.WorldManager;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class WorldMapScene extends AbstractScene {
    public static final WorldMapScene singleton = new WorldMapScene();

    public WorldMapScene() {
        super(WorldMapScene.class.getSimpleName(), WorldMapCityRegulation::new, WorldMapPointRegulation::new);

    }

    @Override
    public WorldMapCityRegulation getCityRegulation() {
        return (WorldMapCityRegulation) cityRegulation;
    }

    @Override
    public void requireThreadSafe() {
        WorldManager.requireWorldThread();
    }

    @Override
    public void submit(Runnable runnable) {
        WorldManager.getInstance().submit(runnable);
    }

    @Override
    public <T> CompletableFuture<T> submit(Supplier<T> supplier) {
        return WorldManager.getInstance().submit(supplier);
    }

    @Override
    public <T> T execute(Supplier<T> supplier) {
        return WorldManager.getInstance().execute(supplier);
    }
}
