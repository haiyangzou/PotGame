package org.pot.gateway.log;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GatewayLogger {
    GUEST,
    REMOTE,
    ;

    public final Logger logger;

    GatewayLogger() {
        logger = LoggerFactory.getLogger(this.name());
        if (logger instanceof ch.qos.logback.classic.Logger) {
            String additivity = System.getProperty("logback.logger.additivity");
            if (StringUtils.isNotBlank(additivity)) {
                ((ch.qos.logback.classic.Logger) logger).setAdditive(BooleanUtils.toBoolean(additivity));
            }
        }
    }
}