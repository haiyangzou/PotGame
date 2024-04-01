package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

@Getter
public class ExecutorConfig {
    private final int queueMaxSize = 1_000_000;
    private final int coreThreadSize = 19;
    private final int maxThreadSize = 199;
    private final int keepAliveTime = 300;
    private String threadName = "Undefined";
    private final TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;

    public static ExecutorConfig loadExecutorConfig(String prefix, Configuration config) {
        ExecutorConfig asyncExecutorConfig = new ExecutorConfig();
        asyncExecutorConfig.setProperties(prefix, config);
        return asyncExecutorConfig;
    }

    private void setProperties(String prefix, Configuration config) {
        threadName = config.getString(prefix + ".async.threads.name", StringUtils.capitalize(prefix));
    }
}