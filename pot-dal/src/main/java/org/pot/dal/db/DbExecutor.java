package org.pot.dal.db;

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

    int executeUpdate(String sql);


}
