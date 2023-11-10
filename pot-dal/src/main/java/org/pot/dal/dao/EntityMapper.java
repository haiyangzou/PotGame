package org.pot.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.pot.dal.dao.handler.TypeHandler;
import org.pot.dal.db.EntityParser;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public abstract class EntityMapper<E> implements EntityParser<E> {
    protected final String sqlSelectAll;
    protected final TableMetas.TableMeta tableMeta;
    protected TypeHandler[] typeHandlers;
    protected final String sqlInsertInDuplicateKeyUpdate;
    protected final String sqlDeleteByPrimaryKey;
    protected final String sqlUpdateByPrimaryKey;
    protected final String sqlSelectByPrimaryKey;
    protected final String sqlInsert;

    public EntityMapper(Class<E> entityClass) {
        this.tableMeta = TableMetas.of(entityClass);
        TableMetas.ColumnMeta[] columnMetas = tableMeta.getColumn();
        int pkCount = tableMeta.countPrimaryKeyColumn();
        if (pkCount <= 0) {
            log.error("{} no field found annotated with ", entityClass);
        }
        int columns = columnMetas.length;
        for (int i = 0; i < columns; i++) {

        }
        this.sqlInsert = SqlBuilder.insert(tableMeta);
        this.sqlInsertInDuplicateKeyUpdate = SqlBuilder.insertOnKeyDuplicate(tableMeta);
        this.sqlDeleteByPrimaryKey = SqlBuilder.deleteByPrimaryKey(tableMeta);
        this.sqlUpdateByPrimaryKey = SqlBuilder.updateByPrimaryKey(tableMeta);
        this.sqlSelectByPrimaryKey = SqlBuilder.selectByPrimaryKey(tableMeta);
        this.sqlSelectAll = SqlBuilder.select(tableMeta);

    }

    public final List<E> selectAll(SqlSession sqlSession) {
        return sqlSession.executeQueryList(this, sqlSelectAll);
    }

    public final int insertOnDuplicateKeyUpdate(SqlSession sqlSession, E entity) {
        return sqlSession.executeUpdate(sqlInsertInDuplicateKeyUpdate, statement -> setParam(statement, entity));
    }

    private void setParam(PreparedStatement statement, E entity) throws SQLException {
        setParam(statement, entity, 1);
    }

    public abstract void setParam(PreparedStatement statement, E entity, int index) throws SQLException;
}
