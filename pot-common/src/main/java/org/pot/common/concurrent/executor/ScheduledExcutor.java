package org.pot.common.concurrent.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ScheduledExcutor extends ScheduledThreadPoolExecutor {
    private final String name;
    private volatile int queueCapacity = 0;

    public ScheduledExcutor(String name, int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory, new AbortPolicy());
        this.name = name;
    }

    public static ScheduledExcutor newScheduledExecutor(int coreThreads, String poolName) {
        return newScheduledExecutor(coreThreads, poolName, true);
    }

    public static ScheduledExcutor newScheduledExecutor(int coreThreads, String poolName, boolean daemon) {
        ThreadFactory threadFactory = ThreadUtil.newThreadFactory(poolName + "-%d", daemon);
        ScheduledExcutor excutor = new ScheduledExcutor(poolName, coreThreads, threadFactory);
        setPolicy(excutor);
        return excutor;
    }

    public static void setPolicy(ScheduledThreadPoolExecutor scheduler) {
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        ensureQueueCapacity(task);
        return super.decorateTask(runnable, task);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        ensureQueueCapacity(task);
        return super.decorateTask(callable, task);
    }

    private void ensureQueueCapacity(Runnable task) {
        if (queueCapacity > 0 && getQueue().size() > queueCapacity) {
            getRejectedExecutionHandler().rejectedExecution(task, this);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isIdle() {
        return ExecutorUtil.isIdle(this);
    }
}
