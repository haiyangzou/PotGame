package org.pot.core.util;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.date.DateTimeString;
import org.pot.common.function.Ticker;
import org.pot.common.structure.ElapsedTimeMonitor;
import org.pot.common.units.TimeUnitsConst;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SignalLight extends Thread {
    private static final long CHECK_SLOW_MS = 200L;
    private static final Map<String, Long> lightMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> deadLigthMap = new ConcurrentHashMap<>();
    public static final ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor
            .ofDefaultWarm(SignalLight.class.getName(), "ms");

    public static void tick(Ticker ticker) {
        watch(ticker.getTickerName(), ticker::tick);
    }

    public static void setOn(final String name) {
        ExceptionUtil.computeCaller(name, SignalLight.class);
        lightMap.computeIfAbsent(name, key -> System.currentTimeMillis());
    }

    public static void setOff(final String name, final long slow) {
        Long start = lightMap.remove(name);
        check(name, slow, start);
        deadLigthMap.remove(name);
        if (start != null) {
            final long elapsed = System.currentTimeMillis() - start;
            if (elapsed > slow) {
                String caller = ExceptionUtil.computeCaller(name, SignalLight.class);
                elapsedTimeMonitor.recordElapsedTime(name, elapsed, caller);
            }
        }
    }

    public static void setOff(final String name) {
        Long start = lightMap.remove(name);
        check(name, Constants.RUN_SLOW_MS, start);
        deadLigthMap.remove(name);
        if (start != null) {
            final long elapsed = System.currentTimeMillis() - start;
            if (elapsed > Constants.RUN_SLOW_MS) {
                String caller = ExceptionUtil.computeCaller(name, SignalLight.class);
                elapsedTimeMonitor.recordElapsedTime(name, elapsed, caller);
            }
        }
    }

    public static <T> void watch(final String name, T param, Consumer<T> consumer) {
        watch(name, Constants.RUN_SLOW_MS, param, consumer);
    }

    public static <T> void watch(final String name, final long slow, Runnable runnable) {
        setOn(name);
        try {
            runnable.run();
        } finally {
            setOff(name, slow);
        }

    }

    public static <T> void watch(final String name, final long slow, T param, Consumer<T> consumer) {
        setOn(name);
        try {
            consumer.accept(param);
        } finally {
            setOff(name, slow);
        }

    }

    public static void watch(final String name, Runnable runnable) {
        setOn(name);
        try {
            runnable.run();
        } finally {
            setOff(name);
        }
    }

    private SignalLight() {
        this.setDaemon(true);
        this.setName(this.getClass().getSimpleName());
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ThreadUtil.run(CHECK_SLOW_MS, this::tick);
            } catch (Throwable e) {
                log.error("Signal Light Error", e);
            }
        }
    }

    private static void check(String name, final long slow, Long start) {
        if (start == null) {
            String caller = ExceptionUtil.computeCaller(name, SignalLight.class);
            log.error("Signal Light Illegal Exception: name={}", name, caller);
            return;
        }
        final long elapsed = System.currentTimeMillis() - start;
        if (elapsed > TimeUnitsConst.MILLIS_OF_MINUTE) {
            String caller = ExceptionUtil.computeCaller(name, SignalLight.class);
            String startAt = DateTimeString.DATA_TIME_MILLIS_ZONE.toString(start);
            String deadLight = String.format("Signal Light Dead:startAt=%s,elapsed=%s,name=%s,caller=%s", startAt,
                    elapsed, name, caller);
            deadLigthMap.put(name, start);
            log.error(deadLight);
        } else if (elapsed > slow) {
            String caller = ExceptionUtil.computeCaller(name, SignalLight.class);
            log.warn("Signal Light Slow: elapsed={}ms,name={},caller={}", elapsed, name, caller);
        }
    }

    private void tick() {
        lightMap.forEach((name, start) -> check(name, CHECK_SLOW_MS, start));
    }
}
