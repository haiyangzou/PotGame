package org.pot.game.engine.player;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.game.engine.GameEngine;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public final class PlayerAsyncTask {
    public static final int PERIOD_MS = 50;//周期时间（毫秒）
    public static final int EXEC_COUNT = 3; //总共执行3次，3次失败就丢弃
    private static volatile ScheduledFuture<?> future;
    private static final LinkedBlockingDeque<Task> task = new LinkedBlockingDeque<>();

    synchronized static void init() {
        if (future == null || future.isDone()) {
            future = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(PlayerAsyncTask::run, PERIOD_MS, PERIOD_MS, TimeUnit.MILLISECONDS);
        }
    }

    public static void submit(long playerId, Consumer<Player> runnable) {
        String caller = ExceptionUtil.computeCaller(runnable, PlayerAsyncTask.class);
        if (!PlayerManager.getInstance().isPlayerExists(playerId)) {
            return;
        }
        task.offer(new Task(playerId, runnable));
    }

    private static void run() {

    }

    private static class Task {
        private final long playerId;
        private final Consumer<Player> runnable;
        private final AtomicInteger count = new AtomicInteger(0);

        public Task(long playerId, Consumer<Player> runnable) {
            this.playerId = playerId;
            this.runnable = runnable;
        }

        private void run(Player player) {
            log.info("");
            runnable.accept(player);
            player.setSkipLoadTimeCheckOnce();
            log.info("");
        }
    }
}
