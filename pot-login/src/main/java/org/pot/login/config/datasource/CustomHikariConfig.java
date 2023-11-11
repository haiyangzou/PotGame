package org.pot.login.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import org.pot.common.config.DbConfig;
import org.pot.common.util.UrlObject;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class CustomHikariConfig extends HikariConfig {
    @Value("${spring.datasource.hikari.auto-commit:true}")
    private boolean autoCommit;
    @Value("${spring.datasource.connection-init-sql:set names utf8mb4}")
    private String connectionInitSql;
    @Value("${spring.datasource.hikari.connection-test-query:SELECT 1 FROM DUAL}")
    private String connectionTestQuery;
    @Value("${spring.datasource.hikari.minimum-idle:20}")
    private int minimumIdle;
    @Value("${spring.datasource.hikari.maximum-pool-size:100}")
    private int maximumPoolSize;
    @Value("${spring.datasource.hikari.connection-timeout:3000}")
    private long connectionTimeout;
    @Value("${spring.datasource.hikari.leak-detection-threshold:30000}")
    private long leakDetectionThreshold;

    @PostConstruct
    public void postConstruct() throws Exception {
        setAutoCommit(autoCommit);
        setConnectionInitSql(connectionTestQuery);
        setConnectionTestQuery(connectionTestQuery);
        setMinimumIdle(minimumIdle);
        setMaximumPoolSize(maximumPoolSize);
        setConnectionTimeout(connectionTimeout);
        setLeakDetectionThreshold(leakDetectionThreshold);
        setJdbcUrl(DbConfig.rebuildJdbcUrl(getJdbcUrl()));
        UrlObject urlObject = UrlObject.valueOf(getJdbcUrl());
        setPoolName("HikariPool-" + urlObject.getAddress());
        urlObject.getParameters().forEach(this::addDataSourceProperty);
    }

}
