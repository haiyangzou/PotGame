package org.pot.dal.db;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.pot.common.Constants;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.structure.ElapsedTimeRecorder;
import org.pot.common.util.Indicator;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.StringUtil;
import org.pot.common.util.UrlObject;
import org.pot.dal.DbConstants;
import org.pot.dal.dao.function.EntityParserFunction;
import org.pot.dal.dao.function.QueryFunction;
import org.pot.dal.dao.param.ArrayParamSetter;
import org.pot.dal.dao.param.BatchParamSetter;
import org.pot.dal.dao.param.ListParamSetter;
import org.pot.dal.dao.param.ParamSetter;

import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
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
        Map<String, Object> status = new HashMap<>();
        return status;
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql) {
        return executeQueryObject(parser, sql, (Parameter) null);
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql, Object... params) {
        return executeQueryObject(parser, sql, new ArrayParamSetter(params));
    }

    private <T> T queryWithParamSetter(String sql, ParamSetter paramSetter, QueryFunction<T> func) {
        final long startTime = System.nanoTime();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (paramSetter != null) {
                paramSetter.setParam(stmt);
            }
            return func.apply(stmt);
        } catch (Exception ex) {
            throw new DbException(ex);
        } finally {
            record(readIndicator, readElapsedTimeRecorder, startTime, sql);
        }
    }

    private void record(Indicator indicator, ElapsedTimeRecorder elapsedTimeRecorder, long startTime, String sql) {
        String abbreviatedSQL = StringUtil.abbreviate(sql, DbConstants.SLOW_SQL_LENGTH);
        final long elapsedTime = System.nanoTime() - startTime;
        elapsedTimeRecorder.record(elapsedTime, abbreviatedSQL);
        if (Constants.Env.isDebug() || DbConstants.SLOW_SQL) {
            final double elapsedMills = DateTimeUtil.computeNanosToMillis(elapsedTime);
            if (elapsedMills > DbConstants.SLOW_MS) {
                log.warn("SQL SLOW");
            }
        }
        indicator.increment();
    }

    public <T> T executeQueryObject(EntityParser<T> parser, String sql, ParamSetter paramSetter) {
        return queryWithParamSetter(sql, paramSetter, new EntityParserFunction<>(parser));
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql) {
        return executeQueryList(parser, sql, (Parameter) null);
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, Object... params) {
        return executeQueryList(parser, sql, new ArrayParamSetter(params));
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, List params) {
        return executeQueryList(parser, sql, new ListParamSetter(params));
    }

    @Override
    public int executeUpdate(String sql) {
        return executeUpdate(sql, (Parameter) null);
    }

    @Override
    public int executeUpdate(String sql, ParamSetter paramSetter) {
        final long startTime = System.nanoTime();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (paramSetter != null) {
                paramSetter.setParam(stmt);
            }
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DbException(ex);
        } finally {
            record(readIndicator, readElapsedTimeRecorder, startTime, sql);
        }
    }

    @Override
    public int executeUpdate(String sql, Object... params) {
        return executeUpdate(sql, new ArrayParamSetter(params));
    }

    @Override
    public int[] executeBatch(String... sql) {
        if (ArrayUtils.isEmpty(sql)) return ArrayUtils.EMPTY_INT_ARRAY;
        final long startTime = System.nanoTime();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String s : sql) {
                stmt.addBatch(s);
            }
            return stmt.executeBatch();
        } catch (Exception ex) {
            throw new DbException(ex);
        } finally {
            record(readIndicator, readElapsedTimeRecorder, startTime, JsonUtil.toJson(sql));
        }
    }

    @Override
    public int[] executeBatch(String sql, @NonNull BatchParamSetter batchParamSetter) {
        final long startTime = System.nanoTime();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            return batchParamSetter.executeBatch(stmt);
        } catch (Exception ex) {
            throw new DbException(ex);
        } finally {
            record(readIndicator, readElapsedTimeRecorder, startTime, sql);
        }
    }
}
