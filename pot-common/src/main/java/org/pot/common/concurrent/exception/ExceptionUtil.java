package org.pot.common.concurrent.exception;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class ExceptionUtil {
    public static Throwable getRootCase(final Throwable throwable) {
        Throwable superCause = throwable;
        while (superCause != null && superCause.getCause() != null) {
            superCause = superCause.getCause();
        }
        return superCause;
    }


    private static final Map<String, String> callerCache = new ConcurrentSkipListMap<>();

    public static String computeCaller(Object object, Class<?> exceptedClasss) {
        return computeCaller(object.getClass().getName(), exceptedClasss);
    }

    public static String computeCaller(Object object, Class<?> exceptedClass1, Class<?> exceptedClass2) {
        return computeCaller(object.getClass().getName(), exceptedClass1, exceptedClass2);
    }
    public static String computeCaller(String name, Class<?> exceptedClass) {
        String result = callerCache.get(name);
        if (result == null) {
            result = abbreviate(getStackTraceWithDepthByLine(1, callerCache.getClass(), exceptedClass));
            callerCache.put(name, result);
        }
        return result;
    }
    public static String computeCaller(String name, Class<?> exceptedClass1, Class<?> exceptedClass2, Class<?> exceptedClass3) {
        return callerCache.computeIfAbsent(name, k -> abbreviate(getStackTraceWithDepthByLine(1, new Class[] { callerCache.getClass(), exceptedClass1, exceptedClass2, exceptedClass3 })));
    }
    public static String computeCaller(String name, Class<?> exceptedClass1, Class<?> exceptedClass2) {
        return callerCache.computeIfAbsent(name, k -> abbreviate(getStackTraceWithDepthByLine(1, new Class[] { callerCache.getClass(), exceptedClass1, exceptedClass2 })));
    }
    public static String getStackTraceWithDepthByTab(final int depth, Class... exceptedClasss) {
        return getStackTraceWithDepth(depth, "\t", exceptedClasss);
    }

    public static String getStackTraceWithDepthByLine(final int depth, Class... exceptedClasss) {
        return getStackTraceWithDepth(depth, StringUtil.getLineSeparator(), exceptedClasss);
    }

    public static String getStackTraceWithDepth(final int depth, final String retStr,
                                                Class... exceptedClass) {
        Set<String> excepted = Sets.newHashSet(StringUtil.toStringArray(exceptedClass, Class::getName));
        excepted.add(ExceptionUtil.class.getName());
        ArrayList<StackTraceElement> stackTraceElements = Lists.newArrayList(new Throwable().getStackTrace());
        stackTraceElements.removeIf(s -> excepted.contains(s.getClassName()));
        StringBuilder stack = new StringBuilder();

        for (int i = 0; i < depth && i < stackTraceElements.size(); i++) {
            StackTraceElement element = stackTraceElements.get(i);
            stack.append(element.getClassName()).append(".").append(element.getMethodName()).append("(")
                    .append(element.getFileName()).append(":").append(element.getLineNumber()).append(")")
                    .append(retStr);
        }
        return StringUtils.trimToEmpty(stack.toString());
    }

    public static String abbreviate(String stacktrace) {
        return ClassUtil.getAbbreviatedName(stacktrace, 3);
    }

    public static String getCaller(Class<?>... exceptedCLasses) {
        LinkedHashSet<Class<?>> list = new LinkedHashSet<>();
        list.add(HashSet.class);
        list.add(TreeSet.class);
        list.add(HashMap.class);
        list.add(TreeMap.class);
        list.add(ArrayList.class);
        list.add(LinkedList.class);
        list.add(ConcurrentHashMap.class);
        list.add(CopyOnWriteArrayList.class);
        list.add(CopyOnWriteArraySet.class);
        list.add(ConcurrentSkipListMap.class);
        if (ArrayUtils.isNotEmpty(exceptedCLasses)) {
            Collections.addAll(list, exceptedCLasses);
        }
        return abbreviate(ExceptionUtil.getStackTraceWithDepthByLine(1, list.toArray(new Class<?>[0])));
    }
}
