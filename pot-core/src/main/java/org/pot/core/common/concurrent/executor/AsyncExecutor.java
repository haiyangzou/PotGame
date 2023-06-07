package org.pot.core.common.concurrent.executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pot.core.common.exception.ExceptionUtil;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.Async;

import groovyjarjarpicocli.CommandLine.RunAll;

public class AsyncExecutor {
    private final ScheduledExcutor scheduledExcutor;
    private final StandardExecutor threadPoolExcutor;
    private final String name;

    public AsyncExecutor(String name, int coreSize) {
        this.name = name;
        threadPoolExcutor = new StandardExecutor(coreSize, this.name + "Sch");
        scheduledExcutor = ScheduledExcutor.newScheduledExecutor(coreSize, this.name + "Sch");
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable comRunnable, long iniDelay, long period, TimeUnit unit) {
        return scheduleAtFixedRate(10L, comRunnable, iniDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(long slowMills, Runnable comRunnable, long iniDelay, long period,
            TimeUnit unit) {
        return scheduledExcutor.scheduleAtFixedRate(wrap(slowMills, comRunnable), iniDelay, period, unit);
    }

    private Runnable wrap(long slowMills, Runnable comRunnable) {
        String caller = ExceptionUtil.computeCaller(comRunnable, AsyncExecutor.class);

        return new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                long mills = System.currentTimeMillis();
                try {
                    comRunnable.run();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            @Override
            public String toString() {
                return super.toString() + "@Caller = " + caller;
            }
        };
    }
}