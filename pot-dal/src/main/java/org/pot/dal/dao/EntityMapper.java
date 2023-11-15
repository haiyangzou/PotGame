package org.pot.dal.dao;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mysql.cj.MysqlType;
import lombok.extern.slf4j.Slf4j;
import org.pot.dal.dao.handler.TypeHandler;
import org.pot.dal.dao.handler.*;
import org.pot.dal.dao.param.EntityBatchParamSetter;
import org.pot.dal.db.EntityParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class EntityMapper<E> implements EntityParser<E> {
    protected static final Logger logger = LoggerFactory.getLogger(EntityMapper.class);
    protected final String sqlSelectAll;
    protected final TableMetas.TableMeta tableMeta;
    @SuppressWarnings("rawtypes")
    protected TypeHandler[] typeHandler;
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
        typeHandler = new TypeHandler[columns];
        for (int i = 0; i < columns; i++) {
            String propertyName = columnMetas[i].getPropertyName();
            Field field = getField(entityClass, propertyName);
            if (field == null) {
                continue;
            }
            typeHandler[i] = getTypeHandlerByAnnotation(field);
            if (typeHandler[i] != null) {
                continue;
            }
            typeHandler[i] = getTypeHandlerByMysqlType(field, columnMetas[i].getMysqlType());
            if (typeHandler[i] != null) {
                continue;
            }
            typeHandler[i] = TypeHandlers.of(field.getType());
            if (typeHandler[i] != null) {
                typeHandler[i] = TypeHandlers.OBJECT_TYPE_HANDLER;
            }
        }
        this.sqlInsert = SqlBuilder.insert(tableMeta);
        this.sqlInsertInDuplicateKeyUpdate = SqlBuilder.insertOnKeyDuplicate(tableMeta);
        this.sqlDeleteByPrimaryKey = SqlBuilder.deleteByPrimaryKey(tableMeta);
        this.sqlUpdateByPrimaryKey = SqlBuilder.updateByPrimaryKey(tableMeta);
        this.sqlSelectByPrimaryKey = SqlBuilder.selectByPrimaryKey(tableMeta);
        this.sqlSelectAll = SqlBuilder.select(tableMeta);
    }

    private TypeHandler getTypeHandlerByAnnotation(Field field) {
        org.pot.dal.dao.TypeHandler handler = field.getAnnotation(org.pot.dal.dao.TypeHandler.class);
        if (handler != null) {
            try {
                Class<? extends TypeHandler<?>> handlerClass = handler.using();
                return handlerClass.newInstance();
            } catch (Throwable cause) {
                log.error("failed to create type handler of" + field.getName(), cause);
            }
        }
        return null;
    }

    private Class<?> getJavaClass(MysqlType mysqlType) {
        if (mysqlType != null) {
            try {
                Field javaClass = mysqlType.getClass().getDeclaredField("javaClass");
                boolean accessible = javaClass.isAccessible();
                javaClass.setAccessible(true);
                Class<?> type = (Class<?>) javaClass.get(mysqlType);
                javaClass.setAccessible(accessible);
                return type;
            } catch (Throwable cause) {
                log.error("failed to create type handler of" + mysqlType, cause);
            }
        }
        return null;
    }

    private TypeHandler getTypeHandlerByMysqlType(Field field, MysqlType mysqlType) {
        Class<?> type = getJavaClass(mysqlType);
        if (type == null) {
            return null;
        }
        Class<?> fieldType = field.getType();
        if (type != fieldType) {
            if (type == String.class) {
                if (fieldType.isEnum()) {
                    return new StringToEnumTypeHandler(fieldType, null);
                }
                TypeFactory typeFactory = TypeFactory.defaultInstance();
                if (Collection.class.isAssignableFrom(fieldType)) {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        JavaType elementType = typeFactory.constructType(parameterizedType.getActualTypeArguments()[0]);
                        CollectionType collectionType = typeFactory.constructCollectionType((Class<? extends Collection>) fieldType, elementType);
                        return new JSONTypeHandler(collectionType);
                    }
                }
                if (Map.class.isAssignableFrom(fieldType)) {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Type[] args = parameterizedType.getActualTypeArguments();
                        JavaType keyType = typeFactory.constructType(args[0]);
                        JavaType valueType = typeFactory.constructType(args[1]);
                        MapType mapType = typeFactory.constructMapType((Class<? extends Map>) fieldType, keyType, valueType);
                        return new JSONTypeHandler(mapType);
                    }
                }
                return new JSONTypeHandler(field.getGenericType());
            }
            if (type == Integer.class && fieldType.isEnum()) {
                return new IntToEnumTypeHandler(fieldType);
            }
            return null;
        }
        return TypeHandlers.of(type);
    }

    private Field getField(Class<?> entityClass, String fieldName) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        Class<?> supperClass = entityClass.getSuperclass();
        if (supperClass != Object.class) {
            return getField(supperClass, fieldName);
        }
        return null;
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

    public final int deleteByPrimaryKey(SqlSession sqlSession, E entity) {
        return sqlSession.executeUpdate(sqlDeleteByPrimaryKey, statement -> setPrimaryKeyParam(statement, 1, entity));
    }

    public final int updateByPrimaryKey(SqlSession sqlSession, E entity) {
        return sqlSession.executeUpdate(sqlUpdateByPrimaryKey, statement -> setUpdateByPrimaryParam(statement, entity));
    }

    public abstract void setNonPrimaryKeyParam(PreparedStatement statement, int firstIndex, E entity) throws SQLException;

    public abstract void setPrimaryKeyParam(PreparedStatement statement, int firstIndex, E entity) throws SQLException;

    public void setUpdateByPrimaryParam(PreparedStatement statement, E entity) throws SQLException {
        setNonPrimaryKeyParam(statement, 1, entity);
        int firstPkIndex = tableMeta.countNonPrimaryKeyColumn() + 1;
        setPrimaryKeyParam(statement, firstPkIndex, entity);
    }

    public final int insert(SqlSession sqlSession, E entity) {
        return sqlSession.executeUpdate(sqlInsert, statement -> setParam(statement, entity));
    }

    public final int[] batchInsert(SqlSession sqlSession, Collection<E> entities) {
        return sqlSession.executeBatch(sqlInsert, new EntityBatchParamSetter<>(this::setParam, entities));
    }

    public final int[] batchInsertOnDuplicateKeyUpdate(SqlSession sqlSession, Collection<E> entities) {
        return sqlSession.executeBatch(sqlInsertInDuplicateKeyUpdate, new EntityBatchParamSetter<>(this::setParam, entities));
    }

}
