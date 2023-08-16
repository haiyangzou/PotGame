package org.pot.game.engine.world.module.map;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;
import org.pot.game.engine.world.module.map.born.PlayerBornRule;
import org.pot.game.engine.world.module.map.clean.WorldMapCleaner;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WorldMapModule extends AbstractWorldModule {
    public static WorldMapModule getInstance() {
        return WorldModuleType.WORLD_MAP.getInstance();
    }

    @Getter
    private final WorldMapCleaner cleaner = new WorldMapCleaner();
    @Getter
    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(5));

    private final ImmutableList<RunSignal> validateSignals = ImmutableList.of(
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)),
            new RunSignal(TimeUnit.MINUTES.toMillis(1)), new RunSignal(TimeUnit.MINUTES.toMillis(1)));

    @Override
    public CompletableFuture<?> init() {
        PlayerBornRule.getInstance().init();
        WorldMapScene.instance.init();
        cleaner.init();
        return null;
    }


}
