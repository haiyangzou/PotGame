package org.pot.core.config;

import lombok.Getter;

@Getter
public class NettyConfig {
    private boolean enableNative = true;
    private int workerThreads = 8;
    private int bossThreads = 2;
    private int backlog = 1024;
    private String host = "0.0.0.0";
    private int port = 0;

    public int getConnectionIdleSeconds() {
        return 60;
    }

    public int getConnectionRecvQueueMaxSize() {
        return 1024;
    }

    public int getConnectionEvictSeconds() {
        return 10;
    }
}
