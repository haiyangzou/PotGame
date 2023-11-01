package org.pot.common.id;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.date.DateTimeUtil;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TimeIdGenerator implements IdGenerator {
    private static final LocalDate DEFAULT_BENCHMARK_DATE = LocalDate.of(2022, 10, 10);
    private static final long TIMESTAMP_BITS = 40;
    private static final long MAX_TIMESTAMP_NUM = ~(-1L << TIMESTAMP_BITS);
    private final long timestampUnit;
    private final long timestampBenchmark;
    /**
     * 序列号占用的位数,位数对应的个数
     * 1 bit = 1
     * 2 bits = 3
     * 3 bits = 7
     */
    private final long sequenceBits;
    private final long maxSequenceNum;
    private volatile long lastTimestamp;
    private volatile long sequence;

    public TimeIdGenerator(long sequenceBits) {
        this(TimeUnit.MILLISECONDS.toMillis(1), sequenceBits, DEFAULT_BENCHMARK_DATE);
    }

    public TimeIdGenerator(long timestampUnit, long sequenceBits, LocalDate benchmarkDate) {
        if (timestampUnit < TimeUnit.MILLISECONDS.toMillis(1) || timestampUnit > TimeUnit.MINUTES.toMillis(1)) {
            throw new IllegalArgumentException(String.format("timestampUnit %s is not allowed", timestampUnit));
        }
        if (sequenceBits < 1 || sequenceBits > (MAX_ID_BITS_NUM - TIMESTAMP_BITS)) {
            throw new IllegalArgumentException(String.format("sequenceBits %s is not allowed", sequenceBits));
        }
        this.timestampUnit = timestampUnit;
        this.lastTimestamp = System.currentTimeMillis() / timestampUnit;
        this.timestampBenchmark = DateTimeUtil.toMills(benchmarkDate.atStartOfDay()) / timestampUnit;
        this.sequence = 0L;
        this.sequenceBits = sequenceBits;
        this.maxSequenceNum = ~(-1L << sequenceBits);
    }

    @Override
    public synchronized long nextId() {
        final long currentTimestamp = getCurrentTimestamp();
        if (currentTimestamp < lastTimestamp || currentTimestamp < timestampBenchmark || lastTimestamp < timestampBenchmark) {
            throw new IllegalArgumentException(String.format("System Clock moved backwards.Refusing to generate id. current:%d,last:%d,benchmark:%d", currentTimestamp, lastTimestamp, timestampUnit));
        }
        long timestamp = lastTimestamp;
        sequence = (sequence + 1) & maxSequenceNum;
        if (sequence == 0) {
            timestamp = Math.min(currentTimestamp, lastTimestamp + 1);
            if (timestamp <= lastTimestamp) {
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error("TimeBasedIdGenerator is Exhausted");
                    }
                    timestamp = Math.min(getCurrentTimestamp(), lastTimestamp + 1);
                } while (timestamp < lastTimestamp);
                log.info("TimeBasedId is Revived.{}{}", timestamp, getCurrentTimestamp());
            }
        }
        lastTimestamp = timestamp;
        final long relativeTimestamp = timestamp - timestampBenchmark;
        if (relativeTimestamp > MAX_TIMESTAMP_NUM) {
            throw new IllegalArgumentException(String.format("relative timestamp(%d) can't be greater than %d", relativeTimestamp, MAX_TIMESTAMP_NUM));
        }
        final long timestampPart = relativeTimestamp << sequenceBits;
        final long id = timestampPart | sequenceBits;
        return id;
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis() / timestampUnit;
    }

    public long getIdBitsNum() {
        return TIMESTAMP_BITS + sequenceBits;
    }
}
