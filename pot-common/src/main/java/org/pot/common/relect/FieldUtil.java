package org.pot.common.relect;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class FieldUtil {
    private static final Map<Class<?>, List<Field>> cach1 = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Field>> cach2 = new ConcurrentHashMap<>();

    public static Field[] getAllFields(Class<?> cls) {
        List<Field> allFieldsList = getAllFieldsList(cls);
        return allFieldsList.toArray(ArrayUtils.EMPTY_FIELD_ARRAY);
    }

    public static List<Field> getAllFieldsList(final Class<?> cls) {
        return cach1.computeIfAbsent(cls, FieldUtil::getAllFields0);
    }

    private static List<Field> getAllFields0(final Class<?> cls) {
        List<Field> fieldsIncludeStatic = getAllFieldsIncludeStatic(cls);
        List<Field> fields = fieldsIncludeStatic.stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toList());
        return ImmutableList.copyOf(fields);
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