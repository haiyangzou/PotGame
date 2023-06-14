package org.pot.common.util;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

public class Indicator implements Comparable<Indicator> {
    public static final class Builder {
        private final String name;
        private int granularity = 1000; // 粒度，單位ms,粒度1秒 = 1000L,粒度1分=60000L;
        private int recentValueSize = 10; // 保留最近的記錄值數量
        private boolean cnt = false;
        private boolean sum = false;
        private boolean avg = true;
        private boolean min = false;
        private boolean max = true;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setGranularity(int granularity) {
            this.granularity = granularity;
            return this;
        }

        public Builder setCnt(boolean cnt) {
            this.cnt = cnt;
            return this;
        }

        public Builder setSum(boolean sum) {
            this.sum = sum;
            return this;
        }

        public Builder setAvg(boolean avg) {
            this.avg = avg;
            return this;
        }

        public Builder setMin(boolean min) {
            this.min = min;
            return this;
        }

        public Builder setMax(boolean max) {
            this.max = max;
            return this;
        }

        public Indicator build() {
            if (StringUtils.isBlank(name))
                throw new IllegalStateException("indicator name error");
            return new Indicator(name, granularity, recentValueSize, cnt, sum, avg, min, max);
        }
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    private final String name;
    private final int granularity;
    private final int recentValueSize;
    private final boolean cnt;
    private final boolean sum;
    private final boolean avg;
    private final boolean min;
    private final boolean max;
    private final AtomicLong cntValue = new AtomicLong();

    private final AtomicLong sumValue = new AtomicLong(); // 新增一個AtomicLong
    private final AtomicLong avgValue = new AtomicLong();
    private final AtomicLong minValue = new AtomicLong();
    private final AtomicLong maxValue = new AtomicLong();
    private final AtomicLong minHappenTime = new AtomicLong();
    private final AtomicLong maxHappenTime = new AtomicLong();
    private final NavigableMap<Long, Long> recentValueMap = new ConcurrentSkipListMap<>();

    private Indicator(String name, int granularity, int recentValueSize, boolean cnt, boolean sum, boolean avg,
            boolean min, boolean max) {
        this.name = name;
        this.granularity = granularity;
        this.recentValueSize = recentValueSize;
        this.cnt = cnt;
        this.sum = sum;
        this.avg = avg;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public int getGranularity() {
        return granularity;
    }

    public int getRecentValueSize() {
        return recentValueSize;
    }

    public boolean isCnt() {
        return cnt;
    }

    public boolean isSum() {
        return sum;
    }

    public boolean isAvg() {
        return avg;
    }

    public boolean isMin() {
        return min;
    }

    public boolean isMax() {
        return max;
    }

    @Override
    public int compareTo(final Indicator o) {
        return Long.compare(o.avgValue.get(), avgValue.get());
    }

    public void add(long v) {
        long happendTime = System.currentTimeMillis() / granularity;
        long happenValue = recentValueMap.merge(happendTime, v, Long::sum);
        maxValue.updateAndGet(prevMaxValue -> {
            if (happenValue > prevMaxValue) {
                maxHappenTime.updateAndGet(prevMaxHappenTime -> happendTime);
                return happenValue;
            } else {
                return prevMaxValue;
            }
        });
        while (recentValueMap.size() > Math.max(1, recentValueSize)) {
            Map.Entry<Long, Long> entry = recentValueMap.pollFirstEntry();
            if (entry != null) {
                Long lastHappenTime = entry.getKey();
                Long lastHappenValue = entry.getValue();
                if (lastHappenTime != null && lastHappenValue != null) {
                    long cnt = cntValue.incrementAndGet();
                    long sum = sumValue.addAndGet(lastHappenValue);
                    avgValue.updateAndGet(operand -> sum / cnt);
                    minValue.updateAndGet(prevMinValue -> {
                        if (lastHappenValue < prevMinValue) {
                            minHappenTime.updateAndGet(prevMinHappenTime -> lastHappenTime);
                            return lastHappenTime;
                        } else {
                            return prevMinValue;
                        }
                    });
                }
            }
        }
    }
}
