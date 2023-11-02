package org.pot.common.config;

import lombok.Getter;

@Getter
public class JettyConfig {
    private String host = "0.0.0.0";
    private int port = -1;
    private int threadPoolMinSize = 20;
    private int threadPoolMaxSize = 100;
    private int timeoutSeconds = 30;
}
