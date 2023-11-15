package org.pot.dal.db;

import lombok.NonNull;
import org.pot.dal.dao.param.BatchParamSetter;
import org.pot.dal.dao.param.ParamSetter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface DbExecutor {
    void close();

    String getName();

    Connection getConnection();

    Map<String, Object> getStatus();

    <T> T executeQueryObject(EntityParser<T> parser, String sql);

    <T> T executeQueryObject(EntityParser<T> parser, String sql, Object... params);

    <T> List<T> executeQueryList(EntityParser<T> parser, String sql);

    <T> List<T> executeQueryList(EntityParser<T> parser, String sql, Object... params);

    <T> List<T> executeQueryList(EntityParser<T> parser, String sql, List params);

    <T> List<T> executeQueryList(EntityParser<T> parser, String sql, ParamSetter paramSetter);

    int executeUpdate(String sql);

    int executeUpdate(String sql, ParamSetter paramSetter);

    int executeUpdate(String sql, Object... params);

    int[] executeBatch(String... sql);

    int[] executeBatch(String sql, @NonNull BatchParamSetter batchParamSetter);
}
