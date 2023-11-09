package org.pot.dal.dao;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityMapperBuilder<E> {
    private static final Map<Class, Class<EntityMapper>> mapperClassMap = new ConcurrentHashMap<>();
    private final Class<E> entityClass;
    private final Class<EntityMapper> entityMapperClass;

    public EntityMapperBuilder(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.entityMapperClass = mapperClassMap.computeIfAbsent(entityClass, EntityMapperBuilder::makeEntityMapperClass);
    }


    private static Class<EntityMapper> makeEntityMapperClass(Class entityClass) {
        return null;
    }

    public static <E> EntityMapperBuilder<E> builder(Class<E> entityClass) {
        return new EntityMapperBuilder<>(entityClass);
    }

    @SuppressWarnings("unchecked")
    public EntityMapper<E> build() {
        try {
            Constructor<?> constructor = entityMapperClass.getConstructor(Class.class);
            return (EntityMapper<E>) constructor.newInstance(entityClass);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
