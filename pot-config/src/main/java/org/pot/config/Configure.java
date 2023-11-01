package org.pot.config;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configure {
    String file();

    int ordinal() default DEFAULT_ORDINAL;

    int DEFAULT_ORDINAL = 349572286;
}
