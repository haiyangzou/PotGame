package org.pot.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.function.Function;

public class StringUtil {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String getLineSeparator() {
        return LINE_SEPARATOR;
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
}
