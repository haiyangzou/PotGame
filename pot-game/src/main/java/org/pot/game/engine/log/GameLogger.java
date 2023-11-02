package org.pot.game.engine.log;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GameLogger {
    PLAYER,
    FLOW,
    ;
    public final Logger logger;

    GameLogger() {
        this.logger = LoggerFactory.getLogger(this.name());
        if (logger instanceof ch.qos.logback.classic.Logger) {
            String additivity = System.getProperty("logback.logger.additivity");
            if (StringUtils.isNotBlank(additivity)) {
                ((ch.qos.logback.classic.Logger) logger).setAdditive(BooleanUtils.toBoolean(additivity));
            }
        }

    }
}
