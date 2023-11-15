package org.pot.dal.dao;

import lombok.Getter;
import lombok.NonNull;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.function.Operation;
import org.pot.dal.async.AsyncDbTaskExecutor;
import org.pot.dal.async.AsyncWithoutResultDbTask;
import org.pot.dal.async.AsyncWithoutResultDbTaskCallback;
import org.pot.dal.dao.param.BatchParamSetter;
import org.pot.dal.dao.param.ParamSetter;
import org.pot.dal.db.DbExecutor;
import org.pot.dal.db.EntityParser;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SqlSession implements DbExecutor {
    @Getter
    private final long id;
    private final DbExecutor dbExecutor;
    private final AsyncDbTaskExecutor asyncDbTaskExecutor;

    public SqlSession(long id, DbExecutor dbExecutor, AsyncDbTaskExecutor asyncDbTaskExecutor) {
        this.id = id;
        this.dbExecutor = dbExecutor;
        this.asyncDbTaskExecutor = asyncDbTaskExecutor;
    }

    public <Mapper extends SessionMapper> Mapper getMapper(Class<Mapper> mapperInterface) {
        return SqlSessionSupport.getSessionMapper(mapperInterface, this);
    }

    @Override
    public void close() {

    }

    @Override
    public String getName() {
        return dbExecutor.getName();
    }

    @Override
    public Connection getConnection() {
        return dbExecutor.getConnection();
    }

    @Override
    public Map<String, Object> getStatus() {
        return dbExecutor.getStatus();
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql) {
        return dbExecutor.executeQueryObject(parser, sql);
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql) {
        return dbExecutor.executeQueryList(parser, sql);
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, Object... params) {
        return dbExecutor.executeQueryList(parser, sql, params);
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, List params) {
        return dbExecutor.executeQueryList(parser, sql);
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, ParamSetter paramSetter) {
        return dbExecutor.executeQueryList(parser, sql, paramSetter);
    }

    @Override
    public final int executeUpdate(String sql) {
        return dbExecutor.executeUpdate(sql);
    }

    @Override
    public final int executeUpdate(String sql, ParamSetter paramSetter) {
        return dbExecutor.executeUpdate(sql, paramSetter);
    }

    @Override
    public final int executeUpdate(String sql, Object... params) {
        return dbExecutor.executeUpdate(sql, params);
    }

    @Override
    public int[] executeBatch(String... sql) {
        return dbExecutor.executeBatch(sql);
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql, Object... params) {
        return dbExecutor.executeQueryObject(parser, sql, params);
    }

    public <M extends SessionMapper> void submitWithoutResult(Class<M> mapperClass, Consumer<M> mapperMethod) {
        submitWithoutResult(mapperClass, mapperMethod, null, null);
    }

    public <M extends SessionMapper> void submitWithoutResult(Class<M> mapperClass, Consumer<M> mapperMethod, Operation onSuccess, Operation onFail) {
        if (asyncDbTaskExecutor == null) {
            return;
        }
        String caller = ExceptionUtil.computeCaller(mapperMethod, SqlSession.class);
        AsyncWithoutResultDbTaskCallback callback = new AsyncWithoutResultDbTaskCallback(onSuccess, onFail);
        AsyncWithoutResultDbTask<M> asyncDbTask = new AsyncWithoutResultDbTask<>(caller, this, mapperClass, mapperMethod, callback);
        asyncDbTaskExecutor.submit(asyncDbTask);
    }

    @Override
    public final int[] executeBatch(String sql, @NonNull BatchParamSetter batchParamSetter) {
        return dbExecutor.executeBatch(sql, batchParamSetter);
    }

}
