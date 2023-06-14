package org.pot.common.concurrent.executor;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtil {
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
}
