package org.pot.game.engine.world.module.march;

import org.pot.game.engine.march.March;
import org.pot.game.engine.march.MarchListener;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldMarchModule extends AbstractWorldModule implements MarchListener {
    @Override
    public void onMarchAdded(March march) {

    }

    @Override
    public void onMarchRemoved(March march) {

    }

    @Override
    public void onMarchUpdated(March march) {

    }

    @Override
    public CompletableFuture<?> init() {
        List<March> marches = new ArrayList<>();
        MarchManager marchManager = WorldMapScene.instance.getMarchManager();
        marches.forEach(marchManager::addMarch);
        marchManager.addListener(this);
        return null;
    }

    @Override
    public void initPlayerData() {

    }

    @Override
    public void tick() {

    }
}