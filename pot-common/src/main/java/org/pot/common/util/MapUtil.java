package org.pot.common.util;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapUtil {
    private static final int DEFAULT_MAP_INITIAL_CAPACITY = 1 << 8;

    public static <K, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<? super K> comparator, K key, V value) {
        return put(new TreeMap<>(comparator), key, value);
    }

    public static <K, V> HashMap<K, V> newHashMap(K k1, V v1, K k2, V v2) {
        return put(newHashMap(), k1, v1, k2, v2);
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return newHashMapWithCapacity(DEFAULT_MAP_INITIAL_CAPACITY);
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
