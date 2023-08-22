package org.pot.dal.db;

import org.pot.common.structure.ElapsedTimeRecorder;
import org.pot.common.util.Indicator;
import org.pot.common.util.UrlObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DbExecutorAdapter implements DbExecutor {
    private final String name;
    private static final AtomicInteger INDEX = new AtomicInteger(0);
    private final Indicator readIndicator = Indicator.builder("tps").build();
    private final Indicator writeIndicator = Indicator.builder("tps").build();
    private final ElapsedTimeRecorder readElapsedTimeRecorder;
    private final ElapsedTimeRecorder writeElapsedTimeRecorder;

    public DbExecutorAdapter(String url) {
        this.name = DbExecutor.class.getSimpleName() + "_" + INDEX.getAndIncrement() + "_" + UrlObject.valueOf(url).getAddress();
        readElapsedTimeRecorder = new ElapsedTimeRecorder(name + "_read", "ns");
        writeElapsedTimeRecorder = new ElapsedTimeRecorder(name + "_write", "ns");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getStatus() {
        return null;
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql) {
        return null;
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql, Object... params) {
        return null;
    }
}
