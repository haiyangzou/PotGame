package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;

@Getter
public class JettyConfig {
    private String host = "0.0.0.0";
    private int port = -1;
    private int threadPoolMinSize = 20;
    private int threadPoolMaxSize = 100;
    private int timeoutSeconds = 30;

    public static JettyConfig loadJettyConfig(Configuration config) {
        JettyConfig jettyConfig = new JettyConfig();
        jettyConfig.setProperties(config);
        return jettyConfig;
    }

    private void setProperties(Configuration config) {
    }
}
