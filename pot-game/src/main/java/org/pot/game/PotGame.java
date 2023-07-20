package org.pot.game;

import org.pot.core.Launcher;

/**
 * 游戏服务器，无spring
 */
@Deprecated
public class PotGame {
    public static void main(String[] args) throws Exception {
        System.setProperty("app.config.file", "conf/game-application.properties");
        System.setProperty("logback.logger.additivity", "true");
        Launcher.bootstrap(null);
    }
}
