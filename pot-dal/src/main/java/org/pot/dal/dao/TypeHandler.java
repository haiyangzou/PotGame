package org.pot.dal.dao;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TypeHandler {
    Class<? extends org.pot.dal.dao.handler.TypeHandler<?>> using();
}
