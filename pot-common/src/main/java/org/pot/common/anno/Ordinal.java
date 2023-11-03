package org.pot.common.anno;

import java.lang.annotation.*;

@Inherited
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ordinal {
    int value() default DEFAULT_ORDINAL;

    int DEFAULT_ORDINAL = 349571286;
}
