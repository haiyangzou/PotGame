package org.pot.common.concurrent.executor;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.structure.ElapsedTimeMonitor;
import org.pot.common.util.MathUtil;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Slf4j
public class ThreadUtil {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(ThreadUtil.class.getName(),
            "ms");
    public static final UncaughtExceptionHandler DEFAULT_EXCEPTION_HANDER = (thread, exception) -> log
            .error("Exception in thread" + thread.getName(), exception);

    public static ThreadFactory newThreadFactory(String name, boolean daemon) {
        return newThreadFactory(name, daemon, Thread.NORM_PRIORITY, DEFAULT_EXCEPTION_HANDER);
    }

    public static ThreadFactory newThreadFactory(String name, boolean daemon, int priority,
                                                 UncaughtExceptionHandler handler) {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat(name);
        builder.setDaemon(daemon);
        builder.setPriority(priority);
        builder.setUncaughtExceptionHandler(handler);
        return builder.build();
    }

    public static void run(boolean warn, long intervalMillis, Runnable runnable) throws Throwable {
        final long milliseconds = System.currentTimeMillis();
        final String caller = ExceptionUtil.computeCaller(runnable, ThreadUtil.class);
        try {
            runnable.run();
        } finally {
            final long elapsedMillis = System.currentTimeMillis() - milliseconds;
            if (warn && elapsedMillis > intervalMillis) {
                log.warn("Thread Run Slow:cost={}ms,caller={}", elapsedMillis, caller);
                elapsedTimeMonitor.recordElapsedTime(caller, elapsedMillis);
            }
            final long sleep = intervalMillis - elapsedMillis;
            Thread.sleep(MathUtil.limit(sleep, 0, intervalMillis));
        }
    }

    public static void run(long intervalMillis, Runnable runnable) throws Throwable {
        run(true, intervalMillis, runnable);
    }

    public static void await(long await, TimeUnit unit, Supplier<Boolean> stop) {
        String caller = ExceptionUtil.computeCaller(stop, ThreadUtil.class);
        await(await, unit, stop, caller);
    }

    public static void await(long await, TimeUnit unit, Supplier<Boolean> stop, String cause) {
        try {
            await(await, 0, unit, stop, cause);
        } catch (TimeoutException e) {
            log.error("thread await was timeout", e);
        }
    }

    public static void await(long await, long timeout, TimeUnit unit, Supplier<Boolean> stop, String cause) throws TimeoutException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            long seconds = 0;
            do {
                unit.sleep(await);
                long ms = stopwatch.elapsed().toMillis();
                if (seconds != TimeUnit.MILLISECONDS.toSeconds(ms)) {
                    seconds = TimeUnit.MINUTES.toSeconds(ms);
                }
                if (timeout > 0 && ms >= unit.toMillis(timeout)) {
                    throw new TimeoutException();
                }
            } while (!stop.get());
        } catch (InterruptedException e) {
            log.error("thread await was timeout", e);
        }
    }

    public static void cancel(long await, TimeUnit unit, Future<?> future) {
        if (future != null) {
            future.cancel(false);
            ThreadUtil.await(await, unit, future::isDone);
        }
    }
}
