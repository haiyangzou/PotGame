package org.pot.game.engine.player;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public final class PlayerAsyncTask {
    public static final int PERIOD_MS = 100;//周期时间（毫秒）
    public static final int EXEC_COUNT = 3; //总共执行3次，3次失败就丢弃
    public static final int COUNT = 100;
    private static volatile ScheduledFuture<?> future;
    private static final LinkedBlockingDeque<Task> tasks = new LinkedBlockingDeque<>();

    synchronized static void init() {
        if (future == null || future.isDone()) {
            future = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(PlayerAsyncTask::run, PERIOD_MS, PERIOD_MS, TimeUnit.MILLISECONDS);
        }
    }

    public static void submit(long playerId, Consumer<Player> runnable) {
        submit(true, playerId, runnable);
    }

    public static void submit(boolean checkDatabaseWhenPlayerNotExists, long playerId, Consumer<Player> runnable) {
        String caller = ExceptionUtil.computeCaller(runnable, PlayerAsyncTask.class);
        log.info("Submit Player Async Task,playerId={},caller={}", playerId, caller);
        if (PlayerManager.getInstance().isPlayerExists(playerId)) {
            Task task = new Task(playerId, runnable);
            if (!execute(task)) tasks.offer(task);
        } else {
            if (checkDatabaseWhenPlayerNotExists) {

            }
            tasks.offer(new Task(playerId, runnable));
        }
    }

    private static boolean execute(Task task) {
        Player player = PlayerManager.fetchPlayer(task.playerId);
        if (player == null || player.getState().get() != PlayerState.running) {
            if (task.count.incrementAndGet() <= COUNT) {
                return false;
            } else {
                return true;
            }
        } else {
            player.submit(() -> task.run(player));
            return true;
        }
    }

    private static void run() {
        long inMemPlayerCount = PlayerManager.getInstance().getPlayerCount();
        long loadPlayerCount = PlayerManager.getInstance().getLoadingPlayerCount();
        long memCalcCount = 5000 - (inMemPlayerCount - WorldMapScene.instance.getCityRegulation().getCleaner().getCleanMax());
        long loadCalcCount = 1000 - loadPlayerCount;
        int size = (int) Math.min(memCalcCount, loadCalcCount);
        if (size > 0 && tasks.size() > 0) {
            log.info("Player async task run size:{}", size);
            List<Task> notExecuteTasks = new ArrayList<>(size);
            Task task;
            while (size-- > 0 && (task = tasks.poll()) != null) {
                if (!execute(task)) notExecuteTasks.add(task);
            }
            if (!notExecuteTasks.isEmpty()) {
                notExecuteTasks.forEach(tasks::offerFirst);
            }
        }
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
