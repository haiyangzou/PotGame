package org.pot.common.concurrent.executor;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.structure.ElapsedTimeMonitor;
import org.pot.common.util.MathUtil;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtil {
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
}
