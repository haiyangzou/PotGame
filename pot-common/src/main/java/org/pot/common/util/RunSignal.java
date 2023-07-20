package org.pot.common.util;

import lombok.Getter;

public class RunSignal {
    @Getter
    private final long runIntervalMillis;
    private long nextRunTimeMillis;

    public RunSignal(long runIntervalMillis) {
        this.runIntervalMillis = runIntervalMillis;
    }

    public RunSignal(boolean immediate, long runIntervalMillis) {
        if (runIntervalMillis < 0) {
            throw new IllegalArgumentException("runIntervalMillis must be positive (" + runIntervalMillis + ")");
        }
        //在执行见各种，随机一个值，后续信号按照次值进行固定时间运行，防止频率固定的任务，初始化之后，堆积一起执行
        this.runIntervalMillis = runIntervalMillis;
        long seed = runIntervalMillis + RandomUtil.randomLong(runIntervalMillis);
        if (immediate) {
            this.nextRunTimeMillis = System.currentTimeMillis() - seed;
        } else {
            this.nextRunTimeMillis = System.currentTimeMillis() + seed;
        }
    }

    public boolean signal() {
        long currentTimeMills = System.currentTimeMillis();
        if (nextRunTimeMillis > currentTimeMills) {
            return false;
        }
        long difference = currentTimeMills - nextRunTimeMillis;
        long compensate = (difference / runIntervalMillis) - 1;
        if (compensate > 0) {
            nextRunTimeMillis += (compensate * runIntervalMillis);
        }
        do {
            nextRunTimeMillis += runIntervalMillis;
        } while (nextRunTimeMillis <= currentTimeMills);
        return true;
    }

    public void run(Runnable runnable) {
        if (signal()) {
            runnable.run();
        }
    }
}
