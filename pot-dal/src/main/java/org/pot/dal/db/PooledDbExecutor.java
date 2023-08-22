package org.pot.dal.db;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class PooledDbExecutor extends DbExecutorAdapter {
    private final DataSource dataSource;

    public PooledDbExecutor(String url, DataSource dataSource) {
        super(url);
        this.dataSource = dataSource;
    }

    @Override
    public void close() {
        if (dataSource instanceof Closeable) {
            try {
                ((Closeable) dataSource).close();
            } catch (IOException e) {
                throw new DbException(e);
            }
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
}
