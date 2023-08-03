package org.pot.dal;

import org.apache.commons.math3.primes.Primes;
import org.pot.common.config.DbConfig;
import org.pot.dal.async.AsyncDbTaskExecutor;
import org.pot.dal.async.IAsyncDbTask;
import org.pot.dal.db.DbExecutor;
import org.pot.dal.db.HikariDbExecutor;

public class DbSupport {
    private final DbExecutor dbExecutor;
    private final AsyncDbTaskExecutor asyncDbTaskExecutor;

    public DbSupport(DbConfig dbConfig) {
        dbExecutor = new HikariDbExecutor();
        this.asyncDbTaskExecutor = new AsyncDbTaskExecutor(Primes.nextPrime(dbConfig.getMinimumIdle()));
    }

    public void submit(IAsyncDbTask task) {
        asyncDbTaskExecutor.submit(task);
    }
}
