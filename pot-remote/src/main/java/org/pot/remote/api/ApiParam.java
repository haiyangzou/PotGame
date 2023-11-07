package org.pot.remote.api;

import java.lang.annotation.*;

@Inherited
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParam {
    String value() default "";

    String comment() default "";
}
