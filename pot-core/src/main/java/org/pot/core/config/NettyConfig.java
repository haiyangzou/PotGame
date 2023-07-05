package org.pot.core.config;

public class NettyConfig {

    public int getConnectionIdleSeconds() {
        return 60;
    }

    public int getConnectionRecvQueueMaxSize() {
        return 1024;
    }
}
