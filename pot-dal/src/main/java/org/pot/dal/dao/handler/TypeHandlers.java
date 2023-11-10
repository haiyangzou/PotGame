package org.pot.dal.dao.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeHandlers {
    private static final Map<Class<?>, TypeHandler<?>> cache = new ConcurrentHashMap<>();
    public static final BooleanTypeHandler BOOLEAN_TYPE_HANDLER = new BooleanTypeHandler();
    public static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

    static {
        register(Boolean.class, BOOLEAN_TYPE_HANDLER);
        register(Object.class, OBJECT_TYPE_HANDLER);

    }

    public static <T> TypeHandler<T> of(Class<T> type) {
        return type != null ? (TypeHandler<T>) cache.get(type) : null;
    }

    public static void register(Class<?> type, TypeHandler<?> handler) {
        cache.put(type, handler);
    }
}
