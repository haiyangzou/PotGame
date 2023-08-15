package org.pot.game.engine.world.module.var;

import org.pot.common.util.RunSignal;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WorldVarModule extends AbstractWorldModule {
    public static WorldVarModule getInstance() {
        return WorldModuleType.WORLD_VAR.getInstance();
    }

    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(1));
    private final Map<String, String> varMap = new ConcurrentHashMap<>();
    private volatile boolean changed = false;

    @Override
    public CompletableFuture<?> init() {

        return null;
    }

    @Override
    public void tick() {
        if (saveSignal.signal() || changed) {
            asyncSave();
        }
    }

    private void asyncSave() {

    }
}
