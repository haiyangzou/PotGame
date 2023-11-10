package org.pot.dal;

import com.zaxxer.hikari.HikariConfig;
import org.apache.commons.math3.primes.Primes;
import org.pot.common.config.DbConfig;
import org.pot.common.util.UrlObject;
import org.pot.dal.async.AsyncDbTaskExecutor;
import org.pot.dal.async.IAsyncDbTask;
import org.pot.dal.dao.SessionMapper;
import org.pot.dal.dao.SqlSession;
import org.pot.dal.db.DbExecutor;
import org.pot.dal.db.HikariDbExecutor;

public class DbSupport {
    private final DbExecutor dbExecutor;
    private final AsyncDbTaskExecutor asyncDbTaskExecutor;

    public SqlSession getSqlSession(long id) {
        return new SqlSession(id, dbExecutor, asyncDbTaskExecutor);
    }

    public SqlSession getSqlSession(Object singleton) {
        return new SqlSession(singleton.hashCode(), dbExecutor, asyncDbTaskExecutor);
    }

    public DbSupport(DbConfig dbConfig) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbConfig.getUrl());
        hikariConfig.setUsername(dbConfig.getUsername());
        hikariConfig.setPassword(dbConfig.getPassword());
        hikariConfig.setAutoCommit(dbConfig.isAutoCommit());
        hikariConfig.setConnectionInitSql(dbConfig.getConnectionInitSql());
        hikariConfig.setConnectionTestQuery(dbConfig.getConnectionTestSql());
        hikariConfig.setMinimumIdle(dbConfig.getMinimumIdle());
        hikariConfig.setMaximumPoolSize(dbConfig.getMaximumPoolSize());
        hikariConfig.setConnectionTimeout(dbConfig.getConnectionTimeOut());
        hikariConfig.setLeakDetectionThreshold(dbConfig.getLeakDetectionThreshold());
        UrlObject urlObject = UrlObject.valueOf(hikariConfig.getJdbcUrl());
        hikariConfig.setPoolName("HikariPool-" + urlObject.getAddress());
        urlObject.getParameters().forEach(hikariConfig::addDataSourceProperty);
        dbExecutor = new HikariDbExecutor(hikariConfig.getJdbcUrl(), hikariConfig);
        this.asyncDbTaskExecutor = new AsyncDbTaskExecutor(Primes.nextPrime(dbConfig.getMinimumIdle()));
    }

    public void submit(IAsyncDbTask task) {
        asyncDbTaskExecutor.submit(task);
    }

    public <Mapper extends SessionMapper> Mapper getMapper(Class<Mapper> mapperInterface) {

        return null;
    }

}
