package org.pot.common.task;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.executor.ExecutorUtil;
import org.pot.common.concurrent.executor.StandardExecutor;
import org.pot.common.function.Ticker;
import org.pot.common.structure.ElapsedTimeMonitor;
import org.pot.common.util.DateTimeUnit;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实现叨叨每秒，每分，每小时执行的作用
 */
@Slf4j
public final class PeriodicTaskManager implements Ticker {
    private final long slowMills;
    public static ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(PeriodicTaskManager.class.getName(), "ms");
    private static final AtomicLong INDEX_GENERATOR = new AtomicLong();
    private final StandardExecutor executor = new StandardExecutor(PeriodTask.class.getSimpleName());
    private final Set<TaskAndCounter> tasks = new ConcurrentSkipListSet<>();
    private final Queue<PeriodTask> pendingTasks = new LinkedBlockingQueue<>();
    private volatile long currentSecond, currentMinute, currentHour, currentDay, currentWeek, currentMonth, currentYear;
    private volatile long lastSecond, lastMinute, lastHour, lastDay, lastWeek, lastMonth, lastYear;

    private final class TaskAndCounter implements Comparable<TaskAndCounter> {
        private final long index = INDEX_GENERATOR.getAndIncrement();
        private final AtomicInteger count = new AtomicInteger();
        private final PeriodTask periodTask;

        public TaskAndCounter(PeriodTask periodTask) {
            this.periodTask = periodTask;
        }

        @Override
        public int compareTo(TaskAndCounter o1) {
            return Long.compare(this.index, o1.index);
        }

        private void execute() {
            final long milliseconds = System.currentTimeMillis();
            try {
                periodTask.doPeriodicTask();
            } catch (Exception exception) {
                log.error("periodTask Error:{}", periodTask.getPeriodicTaskName(), exception);
            }
            final long elapsedMillis = System.currentTimeMillis() - milliseconds;
            if (elapsedMillis > slowMills) {
                log.warn("periodTask Slow:cost={}ms,name={}", elapsedMillis, periodTask.getPeriodicTaskName());
                elapsedTimeMonitor.recordElapsedTime(periodTask.getPeriodicTaskName(), elapsedMillis);
            }
        }

        private void asyncExecute() {
            executor.execute(this::execute);
        }
    }

    public PeriodicTaskManager(long slowMills) {
        this.slowMills = slowMills;
        this.catchCurrentInstant();
        this.pastCurrentInstance();
    }

    private void catchCurrentInstant() {
        LocalDateTime now = LocalDateTime.now();
        currentSecond = now.getSecond();
        currentDay = now.getDayOfMonth();
        currentHour = now.getHour();
        currentMonth = now.getMonthValue();
        currentWeek = now.get(WeekFields.ISO.weekOfWeekBasedYear());
        currentYear = now.getYear();
    }

    private void pastCurrentInstance() {
        lastSecond = currentSecond;
        lastDay = currentDay;
        lastHour = currentHour;
        lastMinute = currentMinute;
        lastMonth = currentMonth;
        lastWeek = currentWeek;

    }

    private void addPendingTask() {
        PeriodTask periodTask;
        while ((periodTask = pendingTasks.poll()) != null) {
            tasks.add(new TaskAndCounter(periodTask));
        }
    }

    public void addTask(final PeriodTask periodTask) {
        if (periodTask.getInterval() <= 0) {
            throw new IllegalArgumentException("interval must be positive");
        }
        if (periodTask.getDateTimeUnit() == null) {
            throw new IllegalArgumentException("getDateTimeUnion must be positive");
        }
        pendingTasks.offer(periodTask);
    }

    private boolean isPast(final DateTimeUnit dateTimeUnit) {
        switch (dateTimeUnit) {
            case SECOND:
                return currentSecond != lastSecond || currentMinute != lastMinute || currentHour != lastHour || currentDay != lastDay || currentWeek != lastWeek;
            case MINUTE:
                return currentMinute != lastMinute || currentHour != lastHour || currentDay != lastDay || currentWeek != lastWeek;
            case HOUR:
                return currentHour != lastHour || currentDay != lastDay || currentWeek != lastWeek;
            case DAY:
                return currentDay != lastDay || currentWeek != lastWeek;
            case WEEK:
                return currentWeek != lastWeek;
            case MONTH:
                return currentMonth != lastMonth;
            case YEAR:
                return currentYear != lastYear;
            default:
                return false;
        }
    }

    private boolean execute(final TaskAndCounter taskAndCounter) {
        boolean remove = false;
        final PeriodTask periodTask = taskAndCounter.periodTask;
        if (isPast(periodTask.getDateTimeUnit())) {
            if (taskAndCounter.count.incrementAndGet() >= periodTask.getInterval()) {
                taskAndCounter.count.updateAndGet(operand -> 0);
                remove = periodTask.isDestroyable();
                if (!remove) {
                    if (periodTask.isAsync()) {
                        taskAndCounter.asyncExecute();
                    } else {
                        taskAndCounter.execute();
                    }
                }
            }
        }
        return remove;
    }

    public void shutdown() {
        ExecutorUtil.shutdownExecutor(executor);
    }

    @Override
    public void tick() {
        catchCurrentInstant();
        try {
            addPendingTask();
            for (Iterator<TaskAndCounter> itr = tasks.iterator(); itr.hasNext(); ) {
                TaskAndCounter taskAndCounter = itr.next();
                boolean remove = execute(taskAndCounter);
                if (remove) {
                    itr.remove();
                }
            }
        } finally {
            pastCurrentInstance();
        }
    }
}
