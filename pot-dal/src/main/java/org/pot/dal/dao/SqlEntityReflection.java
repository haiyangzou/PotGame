package org.pot.dal.dao;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.pot.dal.db.DbException;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlEntityReflection {
    private static final Map<Class<?>, Field[]> columns = new ConcurrentHashMap<>();

    private static Field[] getColumns(Class<?> sqlEntityType) {
        return columns.computeIfAbsent(sqlEntityType, k -> FieldUtils.getFieldsWithAnnotation(sqlEntityType, Column.class));
    }

    public static String getTableName(Class<?> sqlEntityType) {
        Table table = sqlEntityType.getAnnotation(Table.class);
        return table == null ? StringUtils.EMPTY : table.name();
    }

    public static SqlAndParams getSelect(Class<?> sqlEntityType, Object... params) {
        Field[] fieldsColumns = getColumns(sqlEntityType);
        List<String> nameColumns = new ArrayList<>();
        List<String> idColumns = new ArrayList<>();
        List<Class> idColumnTypes = new ArrayList<>();
        for (Field field : fieldsColumns) {
            nameColumns.add(field.getAnnotation(Column.class).name());
            if (field.isAnnotationPresent(Id.class)) {
                idColumns.add(field.getAnnotation(Column.class).name());
                idColumnTypes.add(field.getType());
            }
        }
        if (idColumns.isEmpty()) {
            throw new DbException("column count");
        }
        if (ArrayUtils.isEmpty(params)) {
            throw new DbException("column count");
        }
        if (params.length != idColumns.size()) {
            throw new DbException("column count");
        }
        for (int i = 0; i < params.length; i++) {
            Class type = idColumnTypes.get(i);
            if (!TypeUtils.isAssignable(params[i].getClass(), type)) {
                throw new DbException("");
            }
        }
        String tableName = getTableName(sqlEntityType);
        String whereExpression = "`" + StringUtils.join(idColumns, "`=? AND `") + "`=?";
        String selectExpression = "`" + StringUtils.join(nameColumns, "`, `") + "`";
        String pattern = "SELECT %s FROM `%s` WHERE %s";
        String sql = String.format(pattern, selectExpression, tableName, whereExpression);
        return new SqlAndParams(sql, params);
    }
}
