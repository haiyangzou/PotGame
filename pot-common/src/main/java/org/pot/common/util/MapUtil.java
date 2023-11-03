package org.pot.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class MapUtil {
    private static final int DEFAULT_MAP_INITIAL_CAPACITY = 1 << 8;

    public static <K, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<? super K> comparator, K key, V value) {
        return put(new TreeMap<>(comparator), key, value);
    }

    public static <K, V> LinkedHashMap<K, V> toLinkedHashMap(Collection<V> values, Function<V, K> keyFunction) {
        LinkedHashMap<K, V> map = Maps.newLinkedHashMapWithExpectedSize(values.size());
        values.forEach(v -> map.put(keyFunction.apply(v), v));
        return map;
    }

    public static <E, K, V> HashMap<K, V> toHashMap(Collection<E> values, Function<E, K> keyFunction, Function<E, V> valueFunction) {
        HashMap<K, V> map = Maps.newLinkedHashMapWithExpectedSize(values.size());
        values.forEach(v -> map.put(keyFunction.apply(v), valueFunction.apply(v)));
        return map;
    }

    public static <E, K, V> TreeMap<K, V> toTreeMap(@Nullable Comparator<? super K> comparator, Collection<E> values, Function<E, K> keyFunction, Function<E, V> valueFunction) {
        TreeMap<K, V> map = new TreeMap<>(comparator);
        values.forEach(v -> map.put(keyFunction.apply(v), valueFunction.apply(v)));
        return map;
    }

    public static <K, MK, MV> Map<K, Map<MK, MV>> immutableMapMap(Map<K, Map<MK, MV>> map) {
        Map<K, Map<MK, MV>> temp = new LinkedHashMap<>();
        for (Map.Entry<K, Map<MK, MV>> kMapEntry : map.entrySet()) {
            temp.put(kMapEntry.getKey(), ImmutableMap.copyOf(kMapEntry.getValue()));
        }
        return ImmutableMap.copyOf(temp);
    }

    public static <K, V> HashMap<K, V> newHashMap(K k1, V v1, K k2, V v2) {
        return put(newHashMap(), k1, v1, k2, v2);
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return newHashMapWithCapacity(DEFAULT_MAP_INITIAL_CAPACITY);
    }

    public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
        return put(newHashMap(), key, value);
    }

    public static <K, V> HashMap<K, V> newHashMapWithCapacity(int initialCapacity) {
        return new HashMap<>(initialCapacity);
    }

    public static <K, V, M extends Map<K, V>> M put(M map, K k1, V v1) {
        map.put(k1, v1);
        return map;
    }

    public static <K, V, M extends Map<K, V>> M put(M map, K k1, V v1, K k2, V v2) {
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
