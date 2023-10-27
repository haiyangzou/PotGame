package org.pot.web;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableHttpService {
    String path();

    boolean ignoreRequestLog() default false;
}
