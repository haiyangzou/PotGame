package org.pot.gateway;

import org.pot.core.Launcher;

public class PotGateway {
    public static void main(String[] args) throws Exception {
        System.setProperty("app.config.file", "conf/gateway-application.properties");
        System.setProperty("logback.logger.additivity", "true");
//        System.setProperty("user.timezone","UTC");
        Launcher.bootstrap(null);
    }
}
