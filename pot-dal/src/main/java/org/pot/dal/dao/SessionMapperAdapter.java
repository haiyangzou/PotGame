package org.pot.dal.dao;

import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public abstract class SessionMapperAdapter<Entity> implements SessionMapper<Entity> {
    private final Class<Entity> entityClass;
    protected final EntityMapper<Entity> entityEntityMapper;
    @Getter
    private volatile SqlSession sqlSession;

    @SuppressWarnings("unchecked")
    public SessionMapperAdapter() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Actual Type Error");
        }
        this.entityClass = (Class<Entity>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        this.entityEntityMapper = EntityMapperBuilder.builder(entityClass).build();
    }

    @Override
    public void injectSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Class<Entity> getEntityClass() {
        return entityClass;
    }

    @Override
    public int count() {
        String tableName = SqlEntityReflection.getTableName(entityClass);
        String sql = "SELECT count(*) FROM `" + tableName + "`";
        return sqlSession.executeQueryObject(resultSet -> resultSet.getInt(1), sql);
    }

    @Override
    public List<Entity> all() {
        return entityEntityMapper.selectAll(sqlSession);
    }

    @Override
    public void truncate(boolean useDelete) {
        String tableName = SqlEntityReflection.getTableName(entityClass);
        String sql;
        if (useDelete) {
            sql = "delete from `" + tableName + "`";
        } else {
            sql = "truncate table `" + tableName + "`";
        }
        executeUpdate(sql);
    }

    @Override
    public Entity select(Object... params) {
        SqlAndParams sqlAndParams = SqlEntityReflection.getSelect(entityClass, params);
        return executeQueryObject(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public int insert(Entity entity) {
        return entityEntityMapper.insert(sqlSession, entity);
    }

    @Override
    public int[] batchInsert(Collection<Entity> entities) {
        return entityEntityMapper.batchInsert(sqlSession, entities);
    }

    @Override
    public int insertOnDuplicateKeyUpdate(Entity entity) {
        return entityEntityMapper.insertOnDuplicateKeyUpdate(sqlSession, entity);
    }

    @Override
    public int[] batchInsertOnDuplicateKeyUpdate(Collection<Entity> entities) {
        return entityEntityMapper.batchInsertOnDuplicateKeyUpdate(sqlSession, entities);
    }

    @Override
    public int update(Entity entity) {
        return entityEntityMapper.updateByPrimaryKey(sqlSession, entity);
    }

    @Override
    public int delete(Entity entity) {
        return entityEntityMapper.deleteByPrimaryKey(sqlSession, entity);
    }

    @Override
    public Entity parse(ResultSet resultSet) throws SQLException {
        return entityEntityMapper.parse(resultSet);
    }

    protected Entity executeQueryObject(String sql) {
        return sqlSession.executeQueryObject(this, sql);
    }

    protected Entity executeQueryObject(String sql, Object... params) {
        return sqlSession.executeQueryObject(this, sql, params);
    }

    protected int executeUpdate(String sql) {
        return sqlSession.executeUpdate(sql);
    }

    protected int executeUpdate(String sql, Object... params) {
        return sqlSession.executeUpdate(sql, params);
    }
}
