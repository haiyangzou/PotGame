package org.pot.dal.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDbExecutor extends PooledDbExecutor {
    public HikariDbExecutor(String url, HikariConfig hikariConfig) {
        super(url, new HikariDataSource(hikariConfig));
    }
}
