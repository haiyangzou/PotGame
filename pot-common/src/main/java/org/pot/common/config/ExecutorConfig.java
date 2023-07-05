package org.pot.common.config;

import java.util.concurrent.TimeUnit;

import lombok.Getter;

@Getter
public class ExecutorConfig {
    private int queueMaxSize = 1_000_000;
    private int coreThreadSize = 19;
    private int maxThreadSize = 199;
    private int keepAliveTime = 300;
    private String threadName = "Undefined";
    private final TimeUnit keepAlivTimeUnit = TimeUnit.SECONDS;

}