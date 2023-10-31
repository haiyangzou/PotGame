package org.pot.common.task;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.executor.ExecutorUtil;
import org.pot.common.concurrent.executor.StandardExecutor;
import org.pot.common.function.Ticker;
import org.pot.common.structure.ElapsedTimeMonitor;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实现叨叨每秒，每分，每小时执行的作用
 */
@Slf4j
public final class ScheduledTaskManager implements Ticker {
    private final long slowMills;
    public static ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(PeriodicTaskManager.class.getName(), "ms");
    private static final AtomicLong INDEX_GENERATOR = new AtomicLong();
    private final StandardExecutor executor = new StandardExecutor(PeriodTask.class.getSimpleName());
    private final Set<ScheduledTaskWrapper> tasks = new ConcurrentSkipListSet<>();
    private final Queue<ScheduledTaskWrapper> pendingTasks = new LinkedBlockingQueue<>();

    private final class ScheduledTaskWrapper implements Comparable<ScheduledTaskWrapper> {
        private final long index = INDEX_GENERATOR.getAndIncrement();
        private final long executeTime;
        private final ScheduledTask scheduledTask;

        public ScheduledTaskWrapper(long delayMills, ScheduledTask scheduledTask) {
            this.executeTime = System.currentTimeMillis() + delayMills;
            this.scheduledTask = scheduledTask;
        }

        @Override
        public int compareTo(ScheduledTaskWrapper o) {
            if (this.executeTime < o.executeTime) {
                return -1;
            } else if (this.executeTime > o.executeTime) {
                return 1;
            } else {
                return Long.compare(this.index, o.index);
            }
        }

        private void execute() {
            final long milliseconds = System.currentTimeMillis();
            try {
                scheduledTask.execute();
            } catch (Exception exception) {
                log.error("scheduledTask Error:{}", scheduledTask.getScheduledTaskName(), exception);
            }
            final long elapsedMillis = System.currentTimeMillis() - milliseconds;
            if (elapsedMillis > slowMills) {
                log.warn("scheduledTask Slow:cost={}ms,name={}", elapsedMillis, scheduledTask.getScheduledTaskName());
                elapsedTimeMonitor.recordElapsedTime(scheduledTask.getScheduledTaskName(), elapsedMillis);
            }
        }

        private void asyncExecute() {
            executor.execute(this::execute);
        }
    }

    public ScheduledTaskManager(long slowMills) {
        this.slowMills = slowMills;
    }

    private void addPendingTask() {
        ScheduledTaskWrapper scheduledTaskWrapper;
        while ((scheduledTaskWrapper = pendingTasks.poll()) != null) {
            tasks.add(scheduledTaskWrapper);
        }
    }

    public void shutdown() {
        ExecutorUtil.shutdownExecutor(executor);
    }

    @Override
    public void tick() {
        addPendingTask();
        for (Iterator<ScheduledTaskWrapper> itr = tasks.iterator(); itr.hasNext(); ) {
            ScheduledTaskWrapper taskAndCounter = itr.next();
            if (System.currentTimeMillis() < taskAndCounter.executeTime) {
                break;
            }
            ScheduledTask scheduledTask = taskAndCounter.scheduledTask;
            if (scheduledTask.isAsync()) {
                taskAndCounter.asyncExecute();
            } else {
                taskAndCounter.execute();
            }
        }
    }

    public void schedule(final long delayMills, final ScheduledTask scheduledTask) {
        pendingTasks.offer(new ScheduledTaskWrapper(delayMills, scheduledTask));
    }

    public void schedule(final ScheduledTask scheduledTask) {
        schedule(0L, scheduledTask);
    }
}
