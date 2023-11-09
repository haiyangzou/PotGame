package org.pot.dal.dao;


public interface SqlBuilder {
    String build();

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
            this.builder = new StringBuilder();
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
        public String build() {
            return null;
        }
    }
}
