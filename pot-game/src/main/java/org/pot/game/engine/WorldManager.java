package org.pot.game.engine;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.common.concurrent.executor.DelayRunner;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.util.ExDateTimeUtil;
import org.pot.common.util.StringUtil;
import org.pot.core.util.NewDay;
import org.pot.core.util.SignalLight;
import org.pot.game.engine.world.WorldModule;
import org.pot.game.engine.world.WorldModuleType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class WorldManager extends Thread {
    @Getter
    private static final WorldManager instance = new WorldManager();
    private final AsyncRunner asyncRunner = new AsyncRunner(WorldManager.class);
    private volatile boolean shutdown = false;
    private volatile boolean closed = false;
    private final CompletableFuture<Boolean> initialized = new CompletableFuture<>();
    private final DelayRunner delayRunner = new DelayRunner(WorldManager.class);
    private final WorldModule[] modules = WorldModuleType.modules();

    public static boolean isWorldThread() {
        return Thread.currentThread() == WorldManager.getInstance();
    }

    public WorldManager() {
        this.setDaemon(false);
        this.setName(this.getClass().getSimpleName());
    }

    public void init() throws Exception {
        this.start();
        NewDay.addDayTask(() -> submit(this::onDailyReset));
        NewDay.addWeekTask(() -> submit(this::onWeeklyReset));
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, initialized::isDone);
        if (!initialized.get()) {
            throw new ServiceException("init world manager error");
        }
    }

    public void close() {
        shutdown = true;
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> closed);
    }

    @Override
    public void run() {
        try {
            this.initModules();
            this.initialized.complete(true);
        } catch (Throwable throwable) {
            this.initialized.completeExceptionally(throwable);
        }
        long intervalMillis = GameEngine.getInstance().getConfig().getWorldTickIntervalMillis();
        while (!shutdown) {
            try {
                ThreadUtil.run(intervalMillis, this::tick);
            } catch (Throwable ex) {
                log.error("WorldManager run occur an error.", ex);
            }
        }
        try {
            this.tick();//停止之前，再運行一次，防止再sleep期间有任务没有被运行
        } catch (Throwable ex) {
            log.error("WorldManager run occur an error.", ex);
        }
        this.shutdown();
        this.closed = true;
    }

    private void shutdown() {
        for (int i = modules.length - 1; i >= 0; i--) {
            WorldModule module = modules[i];
            String tickerName = module.getTickerName();
            try {
                SignalLight.watch(tickerName, module::shutdown);
            } catch (Throwable cause) {
                log.error("exception while shutdown world module of type:{}", module.getClass().getName(), cause);
            }
        }
    }

    private void tick() {
        SignalLight.setOn("WorldTick");
        tickModule();
        SignalLight.watch("WorldAsyncRunner", asyncRunner::run);
        SignalLight.watch("WorldDelayRunner", delayRunner::run);
        SignalLight.setOff("WorldTick");
    }

    private void tickModule() {
        for (int i = modules.length - 1; i >= 0; i--) {
            WorldModule module = modules[i];
            try {
                SignalLight.tick(module);
            } catch (Throwable cause) {
                log.error("exception while ticking world module of type:{}", module.getClass().getName(), cause);
            }
        }
    }

    public <T> T execute(Supplier<T> supplier) {
        if (isWorldThread()) {
            return supplier.get();
        } else {
            return asyncRunner.execute(supplier);
        }
    }

    private void initModules() {
        for (WorldModule module : modules) {
            try {
                log.info(StringUtil.format("init world module{}", module.getTickerName()));
                CompletableFuture<?> await = module.init();
                if (await == null) {
                    continue;
                }
                ThreadUtil.await(Constants.AWAIT_MS, Constants.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS, await::isDone, StringUtil.format("init world module:{} ...", module.getTickerName()));
            } catch (Throwable cause) {
                throw new ServiceException(StringUtil.format("init world module{} error", module.getTickerName()), cause);
            }
        }
        for (WorldModule module : modules) {
            module.initPlayerData();
        }
        //加载玩家，添加维护保护罩

    }

    private void onWeeklyReset() {
        for (WorldModule module : modules) {
            module.onWeeklyReset(ExDateTimeUtil.getCurDayStart());
        }
    }

    private void onDailyReset() {
        for (WorldModule module : modules) {
            module.onDailyReset(ExDateTimeUtil.getCurDayStart());
        }
    }

    public void submit(Runnable runnable) {
        if (isWorldThread()) {
            runnable.run();
        } else {
            asyncRunner.submit(runnable);
        }
    }
}
