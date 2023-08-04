package org.pot.game.engine;

import lombok.Getter;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.common.concurrent.executor.DelayRunner;

import java.util.concurrent.CompletableFuture;

public class WorldManager extends Thread {
    @Getter
    private static final WorldManager instance = new WorldManager();
    private final AsyncRunner asyncRunner = new AsyncRunner(WorldManager.class);
    private volatile boolean shutdown = false;
    private volatile boolean closed = false;
    private final CompletableFuture<Boolean> initialized = new CompletableFuture<>();
    private final DelayRunner delayRunner = new DelayRunner(WorldManager.class);

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
