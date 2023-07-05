package org.pot.common.structure;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.MathUtil;
import org.pot.common.util.StringUtil;

import lombok.Getter;

@Getter
public class ElapsedTimeRecorder implements Comparable<ElapsedTimeRecorder> {
    private final String name;
    private final String unit;
    private final AtomicLong totalCount = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);
    private final AtomicLong avgTime = new AtomicLong(0);
    private final AtomicLong minTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong minHappen = new AtomicLong();
    private final AtomicReference<Object> minObjectRef = new AtomicReference<>();
    private final AtomicLong maxTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxHappen = new AtomicLong();
    private final AtomicReference<Object> maxObjectRef = new AtomicReference<>();

    public static String toShowString(String title, Collection<ElapsedTimeRecorder> elapseRecorders) {
        int nameLength = 20;
        int numberLength = 11;
        List<ElapsedTimeRecorder> records = CollectionUtil.copyAndSort(elapseRecorders);
        for (ElapsedTimeRecorder recorder : records) {
            nameLength = MathUtil.max(nameLength, recorder.getName().length());
            numberLength = MathUtil.max(numberLength,
                    String.valueOf(recorder.totalCount.get()).length(),
                    (recorder.totalTime.get() + recorder.unit).length(),
                    (recorder.avgTime.get() + recorder.unit).length(),
                    (recorder.minTime.get() + recorder.unit).length(),
                    (recorder.maxTime.get() + recorder.unit).length());
        }
        String headString = toHeadString(nameLength, numberLength);
        StringBuilder stringBuilder = new StringBuilder(headString);
        stringBuilder.append(StringUtil.getLineSeparator());
        for (ElapsedTimeRecorder recorder : records) {
            stringBuilder.append(recorder.toShowString(nameLength, numberLength)).append(StringUtil.getLineSeparator());
        }
        return "";
    }

    private static String toHeadString(final int nameLength, final int numberLength) {
        final String padStr = " ";
        final String delimiter = "|";
        String showString = delimiter + padStr + StringUtils.center("name", nameLength, padStr) + padStr + delimiter;
        showString = showString + padStr + StringUtils.center("count", numberLength, padStr)
                + padStr + delimiter;
        showString = showString + padStr + StringUtils.center("total", numberLength, padStr) + padStr
                + delimiter;
        showString = showString + padStr + StringUtils.center("avg", numberLength, padStr) + padStr
                + delimiter;
        showString = showString + padStr + StringUtils.center("min", numberLength, padStr) + padStr
                + delimiter;
        showString = showString + padStr + StringUtils.center("max", numberLength, padStr) + padStr
                + delimiter;
        return showString;
    }

    private String toShowString(final int nameLength, final int numberLength) {
        final String padStr = " ";
        final String delimiter = "|";
        String showString = delimiter + padStr + StringUtils.leftPad(name, nameLength, padStr) + padStr + delimiter;
        showString = showString + padStr + StringUtils.leftPad(String.valueOf(totalCount.get()), numberLength, padStr)
                + padStr + delimiter;
        showString = showString + padStr + StringUtils.leftPad(totalTime.get() + unit, numberLength, padStr) + padStr
                + delimiter;
        showString = showString + padStr + StringUtils.leftPad(avgTime.get() + unit, numberLength, padStr) + padStr
                + delimiter;
        showString = showString + padStr + StringUtils.leftPad(minTime.get() + unit, numberLength, padStr) + padStr
                + delimiter;
        showString = showString + padStr + StringUtils.leftPad(maxTime.get() + unit, numberLength, padStr) + padStr
                + delimiter;
        if (maxObjectRef.get() != null)
            showString = showString + padStr + maxObjectRef.get();
        return showString;
    }

    public ElapsedTimeRecorder(final String name) {
        this.name = name;
        this.unit = "";
    }

    public ElapsedTimeRecorder(final String name, final String unit) {
        this.name = name;
        this.unit = StringUtils.trimToEmpty(unit);
    }

    public void record(long time) {
        record(time, null);
    }

    public void record(long t, Object object) {
        if (t > 0) {
            long time = totalTime.addAndGet(t);
            long count = totalCount.incrementAndGet();
            avgTime.updateAndGet(operand -> time / count);
            minTime.updateAndGet(prevMinValue -> {
                if (t < prevMinValue) {
                    minHappen.updateAndGet(operand -> System.currentTimeMillis());
                    minObjectRef.updateAndGet(operand -> object);
                    return t;
                } else {
                    return prevMinValue;
                }
            });
            maxTime.updateAndGet(prevMinValue -> {
                if (t > prevMinValue) {
                    maxHappen.updateAndGet(operand -> System.currentTimeMillis());
                    maxObjectRef.updateAndGet(operand -> object);
                    return t;
                } else {
                    return prevMinValue;
                }
            });
        }
    }

    public void reset() {
        totalCount.updateAndGet(operand -> 0);
        totalTime.updateAndGet(operand -> 0);
        avgTime.updateAndGet(operand -> 0);
        minTime.updateAndGet(operand -> Long.MAX_VALUE);
        minHappen.updateAndGet(operand -> 0);
        minObjectRef.updateAndGet(operand -> null);
        maxTime.updateAndGet(operand -> Long.MAX_VALUE);
        maxHappen.updateAndGet(operand -> 0);
        maxObjectRef.updateAndGet(operand -> null);
    }

    @Override
    public int compareTo(ElapsedTimeRecorder o) {
        // 倒序,大的在前面
        int c = Long.compare(o.totalCount.get(), totalCount.get());
        if (c == 0) {
            c = Long.compare(o.avgTime.get(), avgTime.get());
        }
        if (c == 0) {
            c = Long.compare(o.minTime.get(), minTime.get());
        }
        if (c == 0) {
            c = Long.compare(o.maxTime.get(), maxTime.get());
        }
        return c;
    }

}
