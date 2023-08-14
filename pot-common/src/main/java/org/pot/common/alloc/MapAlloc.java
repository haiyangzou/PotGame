package org.pot.common.alloc;

import org.apache.commons.math3.primes.Primes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MapAlloc {
    private static <T extends Map> T newMap(final int power, Function<Integer, T> function) {
        return function.apply(Primes.nextPrime(1 << power));
    }

    public static <T extends Map> T newSmallMap(Function<Integer, T> function) {
        return newMap(8, function);
    }

    public static <K, V> ConcurrentHashMap<K, V> newSmallConcurrentHashMap() {
        return newSmallMap(ConcurrentHashMap::new);
    }
}
