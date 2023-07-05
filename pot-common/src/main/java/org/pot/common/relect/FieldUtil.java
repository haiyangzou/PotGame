package org.pot.common.relect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableList;

public final class FieldUtil {
    private static final Map<Class<?>, List<Field>> cach1 = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Field>> cach2 = new ConcurrentHashMap<>();

    public static List<Field> getAllFields(final Class<?> cls) {
        return cach1.computeIfAbsent(cls, FieldUtil::getAllFields0);
    }

    private static List<Field> getAllFields0(final Class<?> cls) {
        List<Field> fieldsIncludeStatic = getAllFieldsIncludeStatic(cls);
        // List<Field> fields = fieldsIncludeStatic.stream().filter(field -> !Modifier.isStatic());
        return null;
    }

    private static List<Field> getAllFieldsIncludeStatic(final Class<?> cls) {
        return cach2.computeIfAbsent(cls, FieldUtil::getAllFieldsIncludeStatic0);
    }

    private static List<Field> getAllFieldsIncludeStatic0(final Class<?> cls) {
        Class<?> clazz = cls;
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return ImmutableList.copyOf(fields);
    }
}