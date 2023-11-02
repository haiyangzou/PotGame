package org.pot.game.resource;

import org.pot.common.util.SensitiveWordUtil;
import org.pot.config.json.JsonConfigSupport;

public class GameConfigSupport {
    private static volatile JsonConfigSupport jsonConfigSupport;

    public static void init(String configRootPath) {
        jsonConfigSupport = new JsonConfigSupport(configRootPath);
        jsonConfigSupport.load();
        SensitiveWordUtil.load(getBaseDirPath() + "SensitiveWord.txt");
    }

    public static <T> T getConfig(Class<T> configClass) {
        return jsonConfigSupport.getJsonConfig(configClass);
    }

    public static String getBaseDirPath() {
        return jsonConfigSupport.getBaseDirPath();
    }
}
