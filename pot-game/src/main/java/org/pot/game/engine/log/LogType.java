package org.pot.game.engine.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.logback.PeriodTimeBasedFileNamingAndTriggeringPolicy;
import org.pot.common.logback.PeriodTimeBasedType;
import org.pot.game.engine.GameServerInfo;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public enum LogType {
    RLOG {
        @Override
        protected String buildLoggerName(String name) {
            return "data";
        }

        @Override
        protected Appender<ILoggingEvent> buildAppender(String name) {
            String fileNamePattern = "./logs/rlog/1_" + GameServerInfo.getServerId() + "_" + name + ".%d{yyyy-MM-dd-HH-mm}";
            TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy
                    = new PeriodTimeBasedFileNamingAndTriggeringPolicy<>(PeriodTimeBasedType.minutes, 10);
            return createAsyncRollingFileAppender(name, fileNamePattern, triggeringPolicy);
        }
    },
    ;

    public Logger getLogger(String name) {
        String loggerName = buildLoggerName(name);
        if (StringUtils.isBlank(loggerName)) {
            return null;
        }
        Logger logger = container.get(loggerName);
        if (logger != null) {
            return logger;
        }
        synchronized (container) {
            logger = container.get(loggerName);
            if (logger != null) {
                return logger;
            }
            logger = buildLogger(loggerName);
            container.put(loggerName, logger);
        }
        return logger;
    }

    private Logger buildLogger(String name) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(LogType.class.getName() + "[" + this.name() + "]" + "[" + name + "]");
        logger.setAdditive(false);
        logger.addAppender(buildAppender(name));
        return logger;
    }

    private final Map<String, Logger> container = new ConcurrentHashMap<>();

    protected abstract String buildLoggerName(String name);

    protected abstract Appender<ILoggingEvent> buildAppender(String name);

    private static AsyncAppender createAsyncRollingFileAppender(String name, String fileNamePatter, TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        RollingFileAppender<ILoggingEvent> appender = createRollingFileAppender(name, fileNamePatter, triggeringPolicy);
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(context);
        asyncAppender.setName("Async-" + appender.getName());
        asyncAppender.addAppender(appender);
        asyncAppender.setQueueSize(10240);
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.start();
        return asyncAppender;
    }

    private static RollingFileAppender<ILoggingEvent> createRollingFileAppender(String name, String fileNamePatter, TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setAppend(true);
        appender.setPrudent(false);
        appender.setContext(context);
        appender.setName(name + "-Appender");
        appender.setEncoder(createEncoder(context));
        appender.addFilter(createThresholdFilter());
        appender.setRollingPolicy(createTimeBasedRollingPolicy(context, appender, fileNamePatter, triggeringPolicy));
        appender.start();
        return appender;
    }

    private static Filter<ILoggingEvent> createThresholdFilter() {
        ThresholdFilter levelFilter = new ThresholdFilter();
        levelFilter.setLevel(Level.INFO.toString());
        levelFilter.start();
        return levelFilter;
    }

    private static PatternLayoutEncoder createEncoder(LoggerContext context) {
        PatternLayoutEncoder encode = new PatternLayoutEncoder();
        encode.setPattern("%msg%n");
        encode.setCharset(StandardCharsets.UTF_8);
        encode.setContext(context);
        encode.start();
        return encode;
    }

    private static TimeBasedRollingPolicy<ILoggingEvent> createTimeBasedRollingPolicy(LoggerContext context, FileAppender appender, String fileNamePattern, TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy) {
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setMaxHistory(90);
        policy.setFileNamePattern(fileNamePattern);
        policy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);
        policy.setParent(appender);
        policy.setContext(context);
        policy.start();
        return policy;
    }

}
