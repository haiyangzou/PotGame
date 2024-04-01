package org.pot.chat;

import org.pot.core.Launcher;

/**
 * 游戏服务器，无spring
 */
@Deprecated
public class PotChat {
    public static void main(String[] args) throws Exception {
        System.setProperty("app.config.file", "conf/chat-application.properties");
        System.setProperty("logback.logger.additivity", "true");
        Launcher.bootstrap(null);
    }
}
