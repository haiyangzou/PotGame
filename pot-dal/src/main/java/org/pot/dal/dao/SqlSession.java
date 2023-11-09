package org.pot.dal.dao;

import lombok.Getter;
import org.pot.dal.async.AsyncDbTaskExecutor;
import org.pot.dal.db.DbExecutor;
import org.pot.dal.db.EntityParser;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

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
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
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
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql) {
        return null;
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, Object... params) {
        return null;
    }

    @Override
    public <T> List<T> executeQueryList(EntityParser<T> parser, String sql, List params) {
        return null;
    }

    @Override
    public final int executeUpdate(String sql) {
        return dbExecutor.executeUpdate(sql);
    }

    @Override
    public <T> T executeQueryObject(EntityParser<T> parser, String sql, Object... params) {
        return null;
    }
}
