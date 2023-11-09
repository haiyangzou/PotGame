package org.pot.dal.dao;

import org.pot.dal.db.EntityParser;

import java.util.Collection;
import java.util.List;

public interface SessionMapper<Entity> extends EntityParser<Entity> {
    void injectSqlSession(SqlSession sqlSession);

    Class<Entity> getEntityClass();

    int count();

    List<Entity> all();

    void truncate(boolean useDelete);

    Entity select(Object... params);

    int insert(Entity entity);

    int[] batchInsert(Collection<Entity> entities);

    int insertOnDuplicateKeyUpdate(Entity entity);

    int[] batchInsertOnDuplicateKeyUpdate(Collection<Entity> entities);

    int update(Entity entity);

    int delete(Entity entity);
}
