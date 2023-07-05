package org.pot.common.structure;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.units.TimeUnitsConst;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class ElapsedTimeMonitor {
    private final Cache<String, ElapsedTimeRecorder> cache = Caffeine.newBuilder().maximumSize(2999).build();
    private final String title, unit;
    private final long start, warm;
    private volatile boolean ok;

    public ElapsedTimeMonitor(String title) {
        this.title = title;
        this.unit = "";
        this.start = System.currentTimeMillis();
        this.warm = 0L;
        this.ok = true;
    }

    public ElapsedTimeMonitor(String title, long warm, String unit) {
        this.title = title;
        this.unit = StringUtils.trimToEmpty(unit);
        this.start = System.currentTimeMillis();
        this.warm = warm;
        this.ok = false;
    }

    public static ElapsedTimeMonitor ofDefaultWarm(String title, String unit) {
        return new ElapsedTimeMonitor(title, TimeUnitsConst.MILLIS_OF_30_MINUTES, unit);
    }

    public void recordElapsedTime(String key, long elapsed) {
        recordElapsedTime(key, elapsed, null);
    }

    public void recordElapsedTime(String key, long elapsed, Object object) {
        if (ok) {
            ElapsedTimeRecorder recorder = cache.get(key, k -> new ElapsedTimeRecorder(k, unit));
            if (recorder != null) {
                recorder.record(elapsed, object);
            }
        } else {
            if ((System.currentTimeMillis() - start) > warm) {
                ok = true;
            }
        }
    }

    public String toShowString() {
        return ElapsedTimeRecorder.toShowString(title, this.getRecorders());
    }

    public Collection<ElapsedTimeRecorder> getRecorders() {
        return cache.asMap().values();
    }

    public void clear() {
        cache.invalidateAll();
    }
}
