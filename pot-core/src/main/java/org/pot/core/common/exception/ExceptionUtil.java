package org.pot.core.common.exception;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.StringUtils;
import org.pot.core.common.ClassUtil;
import org.pot.core.common.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

public class ExceptionUtil {
    private static final Map<String, String> callerCache = new ConcurrentSkipListMap<>();

    public static String computeCaller(Object object, Class<?> exceptedClasss) {
        return computeCaller(object.getClass().getName(), exceptedClasss);
    }

    public static String computeCaller(String name, Class<?> exceptedClasss) {
        String result = callerCache.get(name);
        if (result == null) {
            result = abbreviate(ExceptionUtil.getStackTraceWithDepthByLine(1, callerCache.getClass(), exceptedClasss));
            callerCache.put(name, result);
        }
        return result;
    }

    public static String getStackTraceWithDepthByLine(final int depth, Class... exceptedClasss) {
        return getStackTraceWithDepthByLine(depth, StringUtil.getLineSeparator(), exceptedClasss);
    }

    public static String getStackTraceWithDepthByTab(final int depth, Class... exceptedClasss) {
        return getStackTraceWithDepthByLine(depth, "\t", exceptedClasss);
    }

    public static String getStackTraceWithDepthByLine(final int depth, final String retStr, Class... exceptedClasss) {
        Set<String> excepted = Sets.newHashSet(StringUtil.toStringArray(exceptedClasss, Class::getName));
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
}
