package org.pot.game.engine.log;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LogFiles.class)
public @interface LogFile {
    LogType logType();

    String logName() default "";
}
