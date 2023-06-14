package org.pot.common.concurrent.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorUtil {
    private static final int DEFAULT_SHUTDOWN_WAIT_TIME = 5;

    public ExecutorUtil() {

    }

    public static boolean shutdownExecutor(ExecutorService executorService) {
        return shutdownExecutor(executorService, DEFAULT_SHUTDOWN_WAIT_TIME);
    }

    public static boolean shutdownExecutor(ExecutorService executorService, long timeout) {
        if (executorService == null) {
            return true;
        }
        executorService.shutdown();
        long timeoutMS = TimeUnit.SECONDS.toMicros(timeout);
        long now = System.currentTimeMillis();
        while (timeoutMS > 0) {
            try {
                return executorService.awaitTermination(timeoutMS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                long newNow = System.currentTimeMillis();
                timeoutMS -= (newNow - now);
                now = newNow;
            }
        }
        return false;
    }

    public static boolean isIdle(ThreadPoolExecutor threadPoolExecutor) {
        return threadPoolExecutor.getQueue().peek() == null && threadPoolExecutor.getActiveCount() == 0;
    }
}