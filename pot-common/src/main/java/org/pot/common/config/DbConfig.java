package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.UrlUtil;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class DbConfig {
    private int minimumIdle = 20;
    private String url;
    private String username;
    private String password;
    private boolean autoCommit = true;
    private String connectionInitSql = "set names utf8mb4";
    private String connectionTestSql = "SELECT 1 FROM DUAL";
    private int maximumPoolSize = 100;
    private long connectionTimeOut = 3000;
    private long leakDetectionThreshold = 30000;

    public static String rebuildJdbcUrl(String jdbcUrl) {
        if (StringUtils.isBlank(jdbcUrl)) {
            throw new IllegalArgumentException("jdbc url is blank");
        }
        Map<String, String> defaultProperties = new LinkedHashMap<>();
        defaultProperties.put("useSSL", "false");
        defaultProperties.put("useUnicode", "true");
        defaultProperties.put("autoReconnect", "true");
        defaultProperties.put("serverTimezone", "UTC");
        defaultProperties.put("characterEncoding", "utf8");
        defaultProperties.put("allowMultiQueries", "true");
        defaultProperties.put("cachePrepStmts", "true");
        defaultProperties.put("useServerPrepStmts", "true");
        defaultProperties.put("prepStmtCacheSize", "1000");
        defaultProperties.put("prepStmtCacheSqlLimit", "2048");
        defaultProperties.put("rewriteBatchedStatements", "true");
        defaultProperties.put("zeroDateTimeBehavior", "convertToNull");
        defaultProperties.put("useLocalSessionState", "true");
        defaultProperties.put("useLocalTransactionState", "true");
        defaultProperties.put("cacheResultSetMetadata", "true");
        defaultProperties.put("cacheServerConfiguration", "true");
        defaultProperties.put("maintainTimeStatus", "false");
        defaultProperties.put("elideSetAutoCommits", "true");
        defaultProperties.putAll(UrlUtil.getUrlParams(jdbcUrl));
        String baseUrl = UrlUtil.getBaseUrl(jdbcUrl);
        return UrlUtil.buildUrl(baseUrl, defaultProperties);
    }

    public static DbConfig loadDbConfig(String prefix, Configuration config) {
        String url = config.getString(prefix + ".jdbc.url", null);
        String useName = config.getString(prefix + "jdbc.username", null);
        String password = config.getString(prefix + "jdbc.password", null);
        if (StringUtils.isAnyBlank(url, useName, password)) {
            return null;
        }
        DbConfig dbConfig = new DbConfig();
        dbConfig.url = rebuildJdbcUrl(url);
        dbConfig.username = useName;
        dbConfig.password = password;
        dbConfig.setProperties(prefix, config);
        return dbConfig;
    }

    private void setProperties(String prefix, Configuration config) {

    }
}
