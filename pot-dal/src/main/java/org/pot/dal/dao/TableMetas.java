package org.pot.dal.dao;

import com.google.common.collect.ImmutableMap;
import com.mysql.cj.MysqlType;
import lombok.Getter;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TableMetas {
    private static final ConcurrentHashMap<Class<?>, TableMeta> cache = new ConcurrentHashMap<>();

    public static TableMeta of(Class<?> entityClass) {
        TableMeta tableMeta = cache.get(entityClass);
        if (tableMeta != null) {
            return tableMeta;
        }
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            return null;
        }
        tableMeta = new TableMeta(entityClass, table);
        cache.putIfAbsent(entityClass, tableMeta);
        return tableMeta;
    }

    @Getter
    public static final class TableMeta implements Iterable<ColumnMeta> {
        private static final MysqlTypeConverter converter = new MysqlTypeConverter();
        private final String tableName;
        private final ColumnMeta[] column;
        private final Class<?> entityClass;

        public TableMeta(Class<?> entityClass, Table table) {
            this.entityClass = entityClass;
            this.tableName = table.name();
            Field[] field = FieldUtils.getFieldsWithAnnotation(entityClass, Column.class);
            this.column = new ColumnMeta[field.length];
            for (int i = 0; i < column.length; i++) {
                column[i] = new ColumnMeta(this, field[i], converter);
            }
        }

        @Override

        public Iterator<ColumnMeta> iterator() {
            return new ArrayIterator(column);
        }
    }

    public static class MysqlTypeConverter implements Function<Column, MysqlType> {
        private static final Map<String, MysqlType> typeMap;

        static {
            Map<String, MysqlType> map = new HashMap<>();
            for (MysqlType type : MysqlType.values()) {
                map.put(type.getName(), type);
            }
            typeMap = ImmutableMap.copyOf(map);
        }

        @Override
        public MysqlType apply(Column column) {
            String definition = column.columnDefinition();
            if (definition.isEmpty()) {
                return null;
            }
            int length = definition.length();
            int end = length;
            for (int i = 0; i < length; i++) {
                char ch = definition.charAt(i);
                if (Character.isAlphabetic(ch)) {
                    continue;
                }
                end = i;
                break;
            }
            String typeName = definition.substring(0, end).toUpperCase();
            return typeMap.get(typeName);
        }
    }

    @Getter
    public static final class ColumnMeta {
        private final TableMeta tableMeta;
        private final String columnName;
        private final String propertyName;
        private final MysqlType mysqlType;
        private final boolean primaryKey;

        public ColumnMeta(TableMeta tableMeta, Field field, MysqlTypeConverter converter) {
            Column column = field.getAnnotation(Column.class);
            this.columnName = column.name();
            this.tableMeta = tableMeta;
            this.propertyName = field.getName();
            this.mysqlType = converter.apply(column);
            this.primaryKey = field.getAnnotation(Id.class) != null;
        }
    }
}
