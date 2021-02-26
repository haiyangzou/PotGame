package org.pot.core.script;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author zouhaiyang
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Script {
    int type();
    String path();
    String name() default "";
}
