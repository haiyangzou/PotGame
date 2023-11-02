package org.pot.core.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;

@Getter
public class NettyConfig {
    private boolean enableNative = true;
    private int workerThreads = 8;
    private int bossThreads = 2;
    private int backlog = 1024;
    private String host = "0.0.0.0";
    private int port = 0;
    private int connectionIdleSeconds = 120;
    private int connectionRecvQueueMaxSize = 1024;
    private int connectionEvictSeconds = 10;
    private int compressThreshold = 2048;
    private int maxFrameLength = 1024 * 1024;

    protected void setProperties(Configuration configuration) {

    }
}
