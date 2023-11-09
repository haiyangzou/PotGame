package org.pot.dal.dao;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlEntityReflection {
    private static final Map<Class<?>, Field[]> columns = new ConcurrentHashMap<>();

    public static String getTableName(Class<?> sqlEntityType) {
        Table table = sqlEntityType.getAnnotation(Table.class);
        return table == null ? StringUtils.EMPTY : table.name();
    }
}
