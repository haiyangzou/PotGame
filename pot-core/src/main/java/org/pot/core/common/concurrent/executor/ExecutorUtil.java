package org.pot.core.common.concurrent.executor;

import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorUtil {
    public ExecutorUtil() {

    }

    public static boolean isIdle(ThreadPoolExecutor threadPoolExecutor) {
        return threadPoolExecutor.getQueue().peek() == null && threadPoolExecutor.getActiveCount() == 0;
    }
}