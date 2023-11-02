package org.pot.game.engine.log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.JsonUtil;
import org.slf4j.Logger;

import java.util.*;

@Slf4j
public class LogManager {
    @Getter
    private static Map<Class<? extends GameLog>, List<Logger>> loggerMap;

    public static void init(Class<?> scope) {
        ImmutableMap.Builder<Class<? extends GameLog>, List<Logger>> loggerMapBuilder = ImmutableMap.builder();
        Set<Class<? extends GameLog>> logClasses = ClassUtil.getSubTypeOf(scope, GameLog.class, ClassUtil::isConcrete);
        for (Class<? extends GameLog> logClass : logClasses) {
            LogFile[] logFiles = logClass.getDeclaredAnnotationsByType(LogFile.class);
            if (logFiles.length == 0) {
                continue;
            }
            Set<Logger> loggers = new LinkedHashSet<>();
            Arrays.stream(logFiles).forEach(logFile -> {
                String logName = logFile.logName();
                LogType logType = logFile.logType();
                Logger logger = logType.getLogger(logName);
                if (logger != null) {
                    loggers.add(logger);
                } else {
                    loggers.add(GameLogger.FLOW.logger);
                }
            });
            loggerMapBuilder.put(logClass, ImmutableList.copyOf(loggers));
        }
        loggerMap = loggerMapBuilder.build();

    }

    public static void log(GameLog log) {
        List<Logger> loggers = loggerMap.get(log.getClass());
        if (loggers != null) {
            loggers.forEach(logger -> logger.info(JsonUtil.toJson(log)));
        }
    }
}
