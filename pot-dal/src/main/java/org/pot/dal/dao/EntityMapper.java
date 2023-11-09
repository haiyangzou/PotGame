package org.pot.dal.dao;

import org.pot.dal.db.EntityParser;

import java.util.List;

public abstract class EntityMapper<E> implements EntityParser<E> {
    protected final String sqlSelectAll;
    protected final TableMetas.TableMeta tableMeta;

    public EntityMapper(Class<E> entityClass) {
        this.tableMeta = TableMetas.of(entityClass);

        this.sqlSelectAll = SqlBuilder.select(tableMeta);
    }

    public final List<E> selectAll(SqlSession sqlSession) {
        return sqlSession.executeQueryList(this, sqlSelectAll);
    }
}
