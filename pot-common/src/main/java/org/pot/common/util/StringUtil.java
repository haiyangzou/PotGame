package org.pot.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringUtil {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String getLineSeparator() {
        return LINE_SEPARATOR;
    }

    public static <T> String join(Iterable<T> iterable, String separator, Function<T, String> function) {
        return join(iterable, null, separator, function);
    }

    public static <T> String join(Iterable<T> iterable, Predicate<T> predicate, String separator, Function<T, String> function) {
        return join(iterable.iterator(), predicate, separator, function);
    }

    public static <T> String join(Iterator<T> iterable, Predicate<T> predicate, String separator, Function<T, String> function) {
        if (iterable == null || !iterable.hasNext()) {
            return StringUtils.EMPTY;
        }
        StringJoiner joiner = new StringJoiner(separator);
        while (iterable.hasNext()) {
            T obj = iterable.next();
            if (obj != null && (predicate == null || predicate.test(obj))) {
                joiner.add(function.apply(obj));
            }
        }
        return joiner.toString();
    }

    public static <S> String[] toStringArray(S[] original, Function<S, String> caster) {
        if (ArrayUtils.isEmpty(original)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        String[] strings = new String[original.length];
        for (int i = 0; i < original.length; i++) {
            strings[i] = caster.apply(original[i]);
        }
        return strings;
    }

    public static String format(String pattern, Object... objects) {
        return MessageFormatter.arrayFormat(pattern, objects).getMessage();
    }

    public static List<String> splitToList(String string, char separator) {
        if (StringUtils.isBlank(string)) return new ArrayList<>();
        return parseStringArray(StringUtils.split(string, separator), s -> s);
    }

    public static List<String> splitToList(String string, String separator) {
        if (StringUtils.isBlank(string)) return new ArrayList<>();
        return parseStringArray(StringUtils.splitByWholeSeparator(string, separator), s -> s);
    }

    public static <T> List<T> parseStringArray(String[] strings, Function<String, T> caster) {
        List<T> list = Lists.newArrayListWithExpectedSize(strings.length);
        for (String str : strings) {
            T t = caster.apply(str);
            if (t != null) list.add(t);
        }
        return list;
    }
}
