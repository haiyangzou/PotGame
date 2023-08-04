package org.pot.common.concurrent.executor;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.structure.ElapsedTimeMonitor;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class AsyncRunner {
    private final Long id;
    private final Class<?> ownerClass;
    private final Queue<Runnable> queue = new LinkedBlockingDeque<>();
    public static final ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(AsyncRunner.class.getName(), "ms");

    public AsyncRunner(Class<?> ownerClass) {
        this(null, ownerClass);
    }

    public AsyncRunner(Long id, Class<?> ownerClass) {
        this.id = id;
        this.ownerClass = ownerClass;
    }

    public void submit(Runnable runnable) {
        submit(Constants.RUN_SLOW_MS, runnable);
    }

    public void run() {
        Runnable task;
        while ((task = queue.poll()) != null) {
            try {
                task.run();
            } catch (Throwable cause) {
                log.error("Async Runner Occur Error: owner={}", ownerClass.getSimpleName(), cause);
            }
        }
    }

    public void submit(long slowMillis, Runnable runnable) {
        String caller = ExceptionUtil.computeCaller(runnable, ownerClass, AsyncRunner.class);
        queue.add(new Runnable() {
            @Override
            public void run() {
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
            }
        });
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
