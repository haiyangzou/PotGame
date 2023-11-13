package org.pot.dal.dao.handler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeHandlers {
    private static final Map<Class<?>, TypeHandler<?>> cache = new ConcurrentHashMap<>();
    public static final BooleanTypeHandler BOOLEAN_TYPE_HANDLER = new BooleanTypeHandler();
    public static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();
    public static final ByteTypeHandler BYTE_TYPE_HANDLER = new ByteTypeHandler();
    public static final IntegerTypeHandler INTEGER_TYPE_HANDLER = new IntegerTypeHandler();
    public static final LongTypeHandler LONG_TYPE_HANDLER = new LongTypeHandler();
    public static final FloatTypeHandler FLOAT_TYPE_HANDLER = new FloatTypeHandler();
    public static final DoubleTypeHandler DOUBLE_TYPE_HANDLER = new DoubleTypeHandler();
    public static final TimestampTypeHandler TIMESTAMP_TYPE_HANDLER = new TimestampTypeHandler();
    public static final SqlDateTypeHandler SQL_DATE_TYPE_HANDLER = new SqlDateTypeHandler();
    public static final DateTypeHandler DATE_TYPE_HANDLER = new DateTypeHandler();
    public static final StringTypeHandler STRING_TYPE_HANDLER = new StringTypeHandler();
    public static final ByteArrayTypeHandler BYTE_ARRAY_TYPE_HANDLER = new ByteArrayTypeHandler();
    public static final BigDecimalTypeHandler BIG_DECIMAL_TYPE_HANDLER = new BigDecimalTypeHandler();
    public static final ShortTypeHandler SHORT_TYPE_HANDLER = new ShortTypeHandler();


    static {
        register(Boolean.class, BOOLEAN_TYPE_HANDLER);
        register(boolean.class, BOOLEAN_TYPE_HANDLER);
        register(Byte.class, BYTE_TYPE_HANDLER);
        register(byte.class, BYTE_TYPE_HANDLER);
        register(Short.class, SHORT_TYPE_HANDLER);
        register(short.class, SHORT_TYPE_HANDLER);
        register(Integer.class, INTEGER_TYPE_HANDLER);
        register(int.class, INTEGER_TYPE_HANDLER);
        register(Long.class, LONG_TYPE_HANDLER);
        register(long.class, LONG_TYPE_HANDLER);
        register(Float.class, FLOAT_TYPE_HANDLER);
        register(float.class, FLOAT_TYPE_HANDLER);
        register(Double.class, DOUBLE_TYPE_HANDLER);
        register(double.class, DOUBLE_TYPE_HANDLER);
        register(BigDecimal.class, BIG_DECIMAL_TYPE_HANDLER);
        register(String.class, STRING_TYPE_HANDLER);
        register(byte[].class, BYTE_ARRAY_TYPE_HANDLER);
        register(Timestamp.class, TIMESTAMP_TYPE_HANDLER);
        register(Date.class, DATE_TYPE_HANDLER);
        register(java.sql.Date.class, SQL_DATE_TYPE_HANDLER);

    }

    public static <T> TypeHandler<T> of(Class<T> type) {
        return type != null ? (TypeHandler<T>) cache.get(type) : null;
    }

    public static void register(Class<?> type, TypeHandler<?> handler) {
        cache.put(type, handler);
    }
}
