package org.pot.common.concurrent.executor;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.structure.ElapsedTimeMonitor;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DelayRunner {
    private final Long id;
    private final Class<?> ownerClass;
    private final Queue<DelayTask> queue = new LinkedBlockingDeque<>();
    public static final ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(AsyncRunner.class.getName(), "ms");
    private final AtomicLong currentTickNumber = new AtomicLong();
    private final long slowMillis;

    private static abstract class DelayTask {
        protected final Runnable runnable;

        protected DelayTask(Runnable runnable) {
            this.runnable = runnable;
        }

        protected abstract boolean run(long time, long tick);
    }

    private static class DelayTimeTask extends DelayTask {
        protected final long delayTime;

        protected DelayTimeTask(long delayTime, Runnable runnable) {
            super(runnable);
            this.delayTime = delayTime;
        }

        @Override
        protected boolean run(long time, long tick) {
            if (delayTime <= time) {
                this.runnable.run();
                return false;
            }
            return true;
        }
    }

    private static class DelayTickTask extends DelayTask {
        protected final long delayTick;

        protected DelayTickTask(long delayTime, Runnable runnable) {
            super(runnable);
            this.delayTick = delayTime;
        }

        @Override
        protected boolean run(long time, long tick) {
            if (delayTick <= time) {
                this.runnable.run();
                return false;
            }
            return true;
        }
    }

    public DelayRunner(Class<?> ownerClass) {
        this(null, ownerClass);
    }

    public DelayRunner(Long id, Class<?> ownerClass) {
        this(id, Constants.RUN_SLOW_MS, ownerClass);
    }

    public DelayRunner(Long id, long slowMillis, Class<?> ownerClass) {
        this.id = id;
        this.ownerClass = ownerClass;
        this.slowMillis = slowMillis;
    }

    public void delayTick(long mills, Runnable runnable) {
        String caller = ExceptionUtil.computeCaller(runnable, ownerClass, AsyncRunner.class);
        queue.add(new DelayTimeTask(currentTickNumber.get() + mills, () -> {
            long now = System.currentTimeMillis();
            try {
                runnable.run();
            } catch (Throwable cause) {
                writeErrorLog(caller, cause);
            }
            long time = System.currentTimeMillis() - now;
            if (time > slowMillis) {
                writeSlowLog(time, caller);
            }
        }));
    }

    public void delayTime(long mills, Runnable runnable) {
        String caller = ExceptionUtil.computeCaller(runnable, ownerClass, AsyncRunner.class);
        queue.add(new DelayTimeTask(System.currentTimeMillis() + mills, () -> {
            long now = System.currentTimeMillis();
            try {
                runnable.run();
            } catch (Throwable cause) {
                writeErrorLog(caller, cause);
            }
            long time = System.currentTimeMillis() - now;
            if (time > slowMillis) {
                writeSlowLog(time, caller);
            }
        }));
    }

    public void run() {
        DelayTask task;
        int size = queue.size();
        long time = System.currentTimeMillis();
        long tick = currentTickNumber.getAndIncrement();
        while (size-- > 0 && (task = queue.poll()) != null) {
            try {
                if (task.run(time, tick)) {
                    queue.add(task);
                }
            } catch (Throwable cause) {
                log.error("Delay Runner Occur Error: owner={}", ownerClass.getSimpleName(), cause);
            }
        }
    }

    private void writeErrorLog(String caller, Throwable cause) {
        if (id == null) {
            log.error("Async Runner Error:owner={},caller={}", ownerClass.getSimpleName(), caller, cause);
        } else {
            log.error("Async Runner Error:id={},caller={}", id, caller, cause);
        }
    }

    private void writeSlowLog(long time, String caller) {
        if (id == null) {
            log.error("Async Runner Slow: cost={}ms, owner={},caller={}", time, ownerClass.getSimpleName(), caller);
        } else {
            log.error("Async Runner Slow: cost={}ms, id={},caller={}", time, id, caller);
        }
        elapsedTimeMonitor.recordElapsedTime(caller, time);
    }
}
