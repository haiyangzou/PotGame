package org.pot.core;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.function.Ticker;
import org.pot.core.engine.EngineConfig;
import org.pot.core.engine.IEngine;
import org.pot.core.util.SignalLight;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AppEngine<T extends EngineConfig> extends Thread implements IEngine<T> {
    @Getter
    private final AsyncExecutor asyncExecutor;
    @Getter
    private final T config;
    private final CountDownLatch starLatch = new CountDownLatch(1);
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    @Getter
    private volatile boolean started = false;
    @Getter
    private volatile boolean shutdown = false;
    @Getter
    private volatile boolean closed = false;

    private final List<Ticker> tickers = new CopyOnWriteArrayList<>();

    protected AppEngine(Class<T> configClass) throws Exception {
        this.config = null;
        this.asyncExecutor = new AsyncExecutor(getConfig().getAsyExecutorConfig());
    }

    @Override
    public final void run() {
        try {
            startup0();
            started = true;
        } catch (Throwable e) {
            log.error("App Start Error", e);
        } finally {
            starLatch.countDown();
        }
        while (!shutdown) {
            try {
                ThreadUtil.run(Constants.RUN_INTERVAL_MS, this::tick);
            } catch (Throwable e) {
                log.error("App Run Error", e);
            }
        }
        try {
            shutdown0();
            closed = true;
        } catch (Throwable e) {
            log.error("App Stop Error", e);
        } finally {
            stopLatch.countDown();
        }
    }

    void shutdown() throws InterruptedException {
        log.info("stop");
        Stopwatch stopwatch = Stopwatch.createStarted();
        this.shutdown = true;
        this.stopLatch.await();
        if (closed) {
            log.info("over");
            log.info("Shutdown used time{}:ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        } else {
            log.error("error");
        }
    }

    private void shutdown0() {
        try {
            doStop();
        } finally {
            asyncExecutor.shutdown();
        }
    }

    private void startup0() throws Throwable {
        doStart();
    }

    private void tick() {
        for (Ticker ticker : tickers) {
            try {
                SignalLight.tick(ticker);
            } catch (Throwable e) {
                log.error("App Ticker Error:{}", ticker.getTickerName(), e);
            }
        }
    }

    void startup() throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        this.start();
        this.starLatch.await();
        if (started) {
            log.info("Startup used time{} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        } else {
            log.error("error");
            System.exit(1);
        }
    }

    protected abstract void doStart() throws Throwable;

    protected abstract void doStop();
}
