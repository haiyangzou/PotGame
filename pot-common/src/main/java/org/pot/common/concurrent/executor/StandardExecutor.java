package org.pot.common.concurrent.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StandardExecutor extends ThreadPoolExecutor {
    private final String name;
    private final int maxSubmittedTaskCount;
    private final AtomicInteger submittedTasksCount;

    public StandardExecutor(int coreThread, String poolName) {
        // constructor implementation
        this(Integer.MAX_VALUE, coreThread, poolName);
    }

    public StandardExecutor(int queueCapacity, int coreThread, String poolName) {
        // constructor implementation
        this(queueCapacity, coreThread, 300, poolName);
    }

    public StandardExecutor(int queueCapacity, int coreThread, long keepAliveTime, String poolName) {
        // constructor implementation
        this(queueCapacity, coreThread, keepAliveTime, TimeUnit.SECONDS, poolName);
    }

    public StandardExecutor(int queueCapacity, int coreThread, long keepAliveTime, TimeUnit unit, String poolName) {
        // constructor implementation
        this(queueCapacity, Math.max(1, coreThread), Math.max(1, coreThread), keepAliveTime, unit, poolName);
    }

    public StandardExecutor(int queueCapacity, int coreThread, int maxThreads, long keepAliveTime, TimeUnit unit,
            String poolName) {
        // constructor implementationF
        this(queueCapacity, coreThread, maxThreads, keepAliveTime, unit, poolName, true);
    }

    public StandardExecutor(int queueCapacity, int coreThread, int maxThreads, long keepAliveTime, TimeUnit unit,
            String poolName, boolean daemon) {
        // constructor implementationF
        super(Math.max(1, coreThread), Math.max(1, maxThreads), keepAliveTime, unit, new LinkedBlockingQueue<>(),
                ThreadUtil.newThreadFactory(poolName + "-%d", daemon));
        this.name = poolName;
        this.submittedTasksCount = new AtomicInteger(0);
        this.maxSubmittedTaskCount = Math.max(queueCapacity, maxThreads * 100);

    }

    @Override
    public void execute(Runnable command) {
        if (maxSubmittedTaskCount > 0 && submittedTasksCount.get() > maxSubmittedTaskCount) {
            getRejectedExecutionHandler().rejectedExecution(command, this);
        } else {
            submittedTasksCount.incrementAndGet();
            super.execute(command);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedTasksCount.decrementAndGet();
    }

    public boolean isIdle() {
        return ExecutorUtil.isIdle(this);
    }

    public String getName() {
        return name;
    }
}
