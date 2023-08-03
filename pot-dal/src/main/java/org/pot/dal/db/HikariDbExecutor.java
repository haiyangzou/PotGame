package org.pot.dal.db;

import java.sql.Connection;
import java.util.Map;

public class HikariDbExecutor implements DbExecutor {
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
    public <T> T executeQueryObject(EntityParser<T> parser, String sql, Object... params) {
        return null;
    }
}
