package org.pot.common.util;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class CollectionUtil {
    public static <T> List<T> copyAndSort(Collection<T> list, Comparator<? super T> c) {
        return sort(Lists.newArrayList(list), c);
    }

    public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
        list.sort(c);
        return list;
    }

    public static <T extends Comparable<? super T>> List<T> copyAndSort(Collection<T> list) {
        return sort(Lists.newArrayList(list));
    }

    public static <T extends Comparable<? super T>> List<T> sort(List<T> list) {
        Collections.sort(list);
        return list;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T extends List<E>, E> T shuffle(T list) {
        Collections.shuffle(list, ThreadLocalRandom.current());
        return list;
    }
}
