package org.pot.common.relect;

import com.google.common.collect.ImmutableList;
import org.pot.common.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConstructorUtil {
    private static final Map<Class<?>, List<Constructor<?>>> cache = new ConcurrentHashMap<>();

    public static <T> T newObjectWithNonParam(final Class<?> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = requireNonParamConstructor(cls);
        return (T) constructor.newInstance();
    }

    private static Constructor<?> requireNonParamConstructor(final Class<?> cls) {
        Constructor<?> constructor = getNoneParamConstructor(cls);
        if (constructor == null) {
            throw new IllegalAccessError(String.format("can't found none param constructor in %s", ClassUtil.getAbbreviatedName(cls)));
        }
        return constructor;
    }

    public static boolean containsNoneParamConstructor(final Class<?> cls) {
        return getNoneParamConstructor(cls) != null;
    }

    public static Constructor getNoneParamConstructor(final Class<?> cls) {
        return getConstructor(cls).stream().filter(c -> c.getParameterCount() == 0).findFirst().orElse(null);
    }

    public static List<Constructor<?>> getConstructor(final Class<?> cls) {
        return cache.computeIfAbsent(cls, ConstructorUtil::getConstructor0);
    }

    public static List<Constructor<?>> getConstructor0(final Class<?> cls) {
        List<Constructor<?>> constructors = new ArrayList<>();
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            constructors.add(constructor);
        }
        return ImmutableList.copyOf(constructors);
    }

    public static Constructor<?> requireNoneParamConstructor(final Class<?> cls) {
        Constructor<?> constructor = getNoneParamConstructor(cls);
        if (constructor == null) {
            throw new IllegalAccessError(String.format("can't found none param in %s", ClassUtil.getAbbreviatedName(cls)));
        }
        return constructor;
    }

}

