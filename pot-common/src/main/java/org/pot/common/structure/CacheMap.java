package org.pot.common.structure;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
public final class CacheMap<K, V> extends LinkedHashMap<K, V> {
    private final int cacheSize;

    public static <K, V> Map<K, V> of(final int cacheSize) {
        if (cacheSize <= 0) throw new IllegalArgumentException("size must be");
        return Collections.synchronizedMap(new CacheMap<>(cacheSize));
    }

    private final BiConsumer<K, V> onRemoveEldestEntry;

    public CacheMap(final int cacheSize) {
        super(cacheSize, 0.75f, true);
        this.onRemoveEldestEntry = null;
        this.cacheSize = cacheSize;
    }

    public CacheMap(int cacheSize, final BiConsumer<K, V> onRemoveEldestEntry) {
        super(cacheSize, 0.75f, true);
        this.onRemoveEldestEntry = onRemoveEldestEntry;
        this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        boolean remove = this.size() > this.cacheSize;
        if (remove && this.onRemoveEldestEntry != null) {
            try {
                onRemoveEldestEntry.accept(eldest.getKey(), eldest.getValue());
            } catch (Throwable throwable) {
                log.error("error", throwable);
            }
        }
        return remove;
    }
}
