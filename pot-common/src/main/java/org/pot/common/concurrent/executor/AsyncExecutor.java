package org.pot.common.concurrent.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.config.ExecutorConfig;
import org.pot.common.util.Indicator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncExecutor {
    private final ScheduledExcutor scheduledExcutor;
    private final StandardExecutor threadPoolExcutor;
    private final String name;
    private final Indicator indicator = Indicator.builder("tps").build();

    public AsyncExecutor(ExecutorConfig config) {
        this.name = config.getThreadName();
        threadPoolExcutor = new StandardExecutor(config.getQueueMaxSize(), config.getCoreThreadSize(),
                config.getMaxThreadSize(), config.getKeepAliveTime(), config.getKeepAlivTimeUnit(), this.name + "Sch");
        scheduledExcutor = ScheduledExcutor.newScheduledExecutor(config.getCoreThreadSize(), this.name + "Sch");
    }

    public AsyncExecutor(String name, int coreSize) {
        this.name = name;
        threadPoolExcutor = new StandardExecutor(coreSize, this.name + "Sch");
        scheduledExcutor = ScheduledExcutor.newScheduledExecutor(coreSize, this.name + "Sch");
    }

    public void execute(Runnable command) {
        execute(10, command);
    }

    public void execute(long slowMills, Runnable command) {
        threadPoolExcutor.execute(wrap(slowMills, command));
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable comRunnable, long iniDelay, long period, TimeUnit unit) {
        return scheduleAtFixedRate(10L, comRunnable, iniDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(long slowMills, Runnable comRunnable, long iniDelay, long period,
                                                  TimeUnit unit) {
        return scheduledExcutor.scheduleAtFixedRate(wrap(slowMills, comRunnable), iniDelay, period, unit);
    }

    private Runnable wrap(long slowMills, Runnable comRunnable) {
        String caller = ExceptionUtil.computeCaller(comRunnable, AsyncExecutor.class);

        return new Runnable() {

            @Override
            public void run() {
                long mills = System.currentTimeMillis();
                indicator.add(1);
                try {
                    comRunnable.run();
                } catch (Exception e) {
                    log.error("Async Executor Error", e);
                }
                long elapsed = System.currentTimeMillis() - mills;
                if (elapsed < slowMills) {
                    log.warn("Aysnc Executor SLow: elapsed={}ms,caller={}", elapsed, caller);
                }
            }

            @Override
            public String toString() {
                return super.toString() + "@Caller = " + caller;
            }
        };
    }

    public void shutdown() {
        shutdownExecutor(threadPoolExcutor);
        shutdownExecutor(scheduledExcutor);
    }

    private void shutdownExecutor(ExecutorService executorService) {
        if (!ExecutorUtil.shutdownExecutor(executorService)) {
            executorService.shutdownNow()
                    .forEach(runnable -> log.warn("{} shutdown not completed task {}", name, runnable));
        }
    }

    public boolean isIdle() {
        return isThreadPoolExecutorIdle() && isScheduledExecutorIdle();
    }

    public boolean isThreadPoolExecutorIdle() {
        return threadPoolExcutor.isIdle();
    }

    public boolean isScheduledExecutorIdle() {
        return scheduledExcutor.isIdle();
    }
}