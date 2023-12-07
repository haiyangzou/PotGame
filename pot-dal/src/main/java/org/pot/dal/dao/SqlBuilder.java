package org.pot.dal.dao;


import org.apache.commons.lang3.StringUtils;

public interface SqlBuilder {
    String build();

    static InsertSqlBuilder insert(String tableName) {
        return new InsertSqlBuilder(tableName);
    }

    static InsertSqlOnDuplicateKeyUpdateBuilder insertOnKeyDuplicate(String tableName) {
        return new InsertSqlOnDuplicateKeyUpdateBuilder(tableName);
    }

    static DeleteSqlBuilder delete(String tableName) {
        return new DeleteSqlBuilder(tableName);
    }

    static UpdateSqlBuilder update(String tableName) {
        return new UpdateSqlBuilder(tableName);
    }

    static String insert(TableMetas.TableMeta tableMeta) {
        InsertSqlBuilder builder = insert(tableMeta.getTableName());
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            builder.column(columnMeta.getColumnName());
        }
        return builder.build();
    }

    static String insertOnKeyDuplicate(TableMetas.TableMeta tableMeta) {
        InsertSqlOnDuplicateKeyUpdateBuilder builder = insertOnKeyDuplicate(tableMeta.getTableName());
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            builder.column(columnMeta.getColumnName());
        }
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            if (!columnMeta.isPrimaryKey()) {
                builder.onDuplicateKeyUpdate(columnMeta.getColumnName());
            }
        }
        return builder.build();
    }

    static String deleteByPrimaryKey(TableMetas.TableMeta tableMeta) {
        DeleteSqlBuilder builder = delete(tableMeta.getTableName());
        WhereSqlBuilder whereSqlBuilder = builder.where();
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            if (!columnMeta.isPrimaryKey()) {
                whereSqlBuilder.andColumnEq(columnMeta.getColumnName(), '?');
            }
        }
        return builder.build();
    }

    static String updateByPrimaryKey(TableMetas.TableMeta tableMeta) {
        UpdateSqlBuilder builder = update(tableMeta.getTableName());
        WhereSqlBuilder whereSqlBuilder = builder.where();
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            builder.column(columnMeta.getColumnName());
        }
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            if (!columnMeta.isPrimaryKey()) {
                whereSqlBuilder.andColumnEq(columnMeta.getColumnName(), '?');
            }
        }
        return builder.build();
    }

    static String selectByPrimaryKey(TableMetas.TableMeta tableMeta) {
        SelectSqlBuilder builder = select(tableMeta.getTableName());
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            builder.column(columnMeta.getColumnName());
        }
        return builder.build();
    }

    static String select(TableMetas.TableMeta tableMeta) {
        SelectSqlBuilder builder = select(tableMeta.getTableName());
        for (TableMetas.ColumnMeta columnMeta : tableMeta) {
            builder.column(columnMeta.getColumnName());
        }
        return builder.build();
    }

    static SelectSqlBuilder select(String tableName) {
        return new SelectSqlBuilder(tableName);
    }

    abstract class AbstractConditionSqlBuilder implements SqlBuilder {
        protected StringBuilder builder;
        protected final WhereSqlBuilder whereSqlBuilder = new WhereSqlBuilder(this);

        public WhereSqlBuilder where() {
            return whereSqlBuilder;
        }

        abstract String build(String conditions);
    }

    class WhereSqlBuilder implements SqlBuilder {
        protected int conditions;
        protected final StringBuilder whereBuilder = new StringBuilder(" WHERE ");
        protected final AbstractConditionSqlBuilder sqlBuilder;

        public WhereSqlBuilder(AbstractConditionSqlBuilder sqlBuilder) {
            this.sqlBuilder = sqlBuilder;
        }

        public WhereSqlBuilder andIsNull(String columnName) {
            if (conditions > 0) {
                whereBuilder.append(" and ");
            }
            whereBuilder.append('`').append(columnName).append('`').append(" IS NULL");
            conditions++;
            return this;
        }

        public WhereSqlBuilder andIsNotNull(String columnName) {
            if (conditions > 0) {
                whereBuilder.append(" and ");
            }
            whereBuilder.append('`').append(columnName).append('`').append(" IS NOT NULL");
            conditions++;
            return this;
        }

        public WhereSqlBuilder andColumnEq(String columnName, Object value) {
            if (value == null) {
                return andIsNull(columnName);
            }
            return and(columnName, "=", value);
        }

        public WhereSqlBuilder and(String condition) {
            if (conditions > 0) {
                whereBuilder.append(" and ");
            }
            whereBuilder.append(condition);
            conditions++;
            return this;
        }

        public WhereSqlBuilder and(String columnName, String op, Object value) {
            if (conditions > 0) {
                whereBuilder.append(" and ");
            }
            whereBuilder.append('`').append(columnName).append('`').append(op).append(valueString(value));
            conditions++;
            return this;
        }

        private String valueString(Object value) {
            if (value instanceof String) {
                return "'" + value + "'";
            }
            return value.toString();
        }

        @Override
        public String build() {
            if (conditions <= 0) {
                return sqlBuilder.build(null);
            }
            return sqlBuilder.build(whereBuilder.toString());
        }
    }

    class SelectSqlBuilder extends AbstractConditionSqlBuilder {
        private int columns;
        private final String tableName;
        private final StringBuilder builder;
        private String groupBy;
        private int limitMin;
        private int limit;

        public SelectSqlBuilder(String tableName) {
            this.tableName = tableName;
            this.builder = new StringBuilder("SELECT ");
        }

        public SelectSqlBuilder limit(int limitMin, int limit) {
            this.limitMin = limitMin;
            this.limit = limit;
            return this;
        }

        public SelectSqlBuilder column(String columnName) {
            if (columns > 0) {
                builder.append(",");
            }
            builder.append('`').append(columnName).append('`');
            columns++;
            return this;
        }

        @Override
        public String build(String conditions) {
            builder.append(" FROM `").append(tableName).append('`');
            if (StringUtils.isNoneBlank(conditions)) {
                builder.append(conditions);
            }
            if (!StringUtils.isBlank(groupBy)) {
                builder.append(" GROUP BY ").append(groupBy);
            }
            if (limitMin > 0 && limit > 0) {
                builder.append(" LIMIT ").append(limitMin).append(',').append(limit);
            } else if (limitMin <= 0 && limit > 0) {
                builder.append(" LIMIT ").append(limit);
            }
            return builder.toString();
        }

        public String build() {
            return whereSqlBuilder.build();
        }
    }

    class InsertSqlBuilder {
        private int columns;
        private final StringBuilder builder;

        public InsertSqlBuilder(String tableName) {
            this.builder = new StringBuilder("INSERT INTO `").append(tableName).append('`');
        }

        public InsertSqlBuilder column(String columnName) {
            if (columns == 0) {
                builder.append("(");
            } else {
                builder.append(",");
            }
            builder.append('`').append(columnName).append('`');
            columns++;
            return this;
        }

        public String build() {
            builder.append(") VALUES (?");
            for (int i = 1; i < columns; i++) {
                builder.append(",?");
            }
            builder.append(")");
            return builder.toString();
        }
    }

    class InsertSqlOnDuplicateKeyUpdateBuilder {
        private int columns;
        private final StringBuilder builder;
        private StringBuilder valuesBuilder;

        public InsertSqlOnDuplicateKeyUpdateBuilder(String tableName) {
            this.builder = new StringBuilder("INSERT INTO `").append(tableName).append('`');
        }

        public InsertSqlOnDuplicateKeyUpdateBuilder onDuplicateKeyUpdate(String columnName) {
            if (valuesBuilder == null) {
                this.valuesBuilder = new StringBuilder(" ON DUPLICATE KEY UPDATE ");
            } else {
                this.valuesBuilder.append(',');
            }
            valuesBuilder.append('`').append(columnName).append("`=VALUES(`").append(columnName).append("`)");
            return this;
        }

        public InsertSqlOnDuplicateKeyUpdateBuilder column(String columnName) {
            if (columns == 0) {
                builder.append("(");
            } else {
                builder.append(",");
            }
            builder.append('`').append(columnName).append('`');
            columns++;
            return this;
        }

        public String build() {
            builder.append(") VALUES (?");
            for (int i = 1; i < columns; i++) {
                builder.append(",?");
            }
            builder.append(")");
            if (valuesBuilder != null) {
                builder.append(valuesBuilder);
            }
            return builder.toString();
        }
    }

    class UpdateSqlBuilder extends AbstractConditionSqlBuilder {
        private int columns;

        public UpdateSqlBuilder(String tableName) {
            this.builder = new StringBuilder("UPDATE `").append(tableName).append('`');
        }

        public UpdateSqlBuilder column(String columnName) {
            if (columns == 0) {
                builder.append(" SET ");
            } else {
                builder.append(",");
            }
            builder.append('`').append(columnName).append('`').append("=?");
            columns++;
            return this;
        }

        @Override
        public String build(String conditions) {
            if (StringUtils.isBlank(conditions)) {
                return builder.toString();
            }
            return builder.append(conditions).toString();
        }

        public String build() {
            return whereSqlBuilder.build();
        }
    }

    class DeleteSqlBuilder extends AbstractConditionSqlBuilder {
        private int columns;

        public DeleteSqlBuilder(String tableName) {
            this.builder = new StringBuilder("DELETE FROM `").append(tableName).append('`');
        }

        @Override
        public String build(String conditions) {
            if (StringUtils.isBlank(conditions)) {
                builder.append(conditions);
            }
            return builder.toString();
        }

        public String build() {
            return whereSqlBuilder.build();
        }
    }

}
