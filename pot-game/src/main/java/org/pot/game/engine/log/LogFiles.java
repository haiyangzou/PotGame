package org.pot.game.engine.log;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogFiles {
    LogFile[] value();
}
