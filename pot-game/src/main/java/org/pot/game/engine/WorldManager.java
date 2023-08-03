package org.pot.game.engine;

import lombok.Getter;
import org.pot.common.concurrent.executor.AsyncRunner;

public class WorldManager extends Thread {
    @Getter
    private static final WorldManager instance = new WorldManager();
    private final AsyncRunner asyncRunner = new AsyncRunner(WorldManager.class);

    public static boolean isWorldThread() {
        return Thread.currentThread() == WorldManager.getInstance();
    }

    public void submit(Runnable runnable) {
        if (isWorldThread()) {
            runnable.run();
        } else {
            asyncRunner.submit(runnable);
        }
    }
}
