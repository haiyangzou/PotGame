package org.pot.common.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RandomUtil {
    public static long randomLong(long bound) {
        return ThreadLocalRandom.current().nextLong(bound);
    }

    public static <E> E randomOne(Collection<E> objects, Function<E, Integer> probabilityFunction) {
        if (CollectionUtil.isEmpty(objects)) return null;
        List<E> list = objects.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(list)) return null;
        int[] probabilities = list.stream().mapToInt(probabilityFunction::apply).toArray();
        int randomIndex = randomIndex(probabilities);
        if (randomIndex >= 0 && randomIndex < list.size()) {
            return list.get(randomIndex);
        }
        return null;
    }

    public static int randomIndex(int[] probabilities) {
        return randomIndexWithExcludedIndex(probabilities, null);
    }

    public static int randomIndexWithExcludedIndex(int[] probabilities, Collection<Integer> exclusion) {
        return randomIndexWithExcludedIndex(Arrays.asList(ArrayUtils.toObject(probabilities)), exclusion);
    }

    public static int randomIndexWithExcludedIndex(List<Integer> probabilities, Collection<Integer> exclusion) {
        if (CollectionUtil.isEmpty(probabilities)) {
            return -1;
        }
        int probabilitySum = 0;
        for (int i = 0; i < probabilities.size(); i++) {
            if (exclusion == null || !exclusion.contains(i)) {
                probabilitySum = probabilitySum + probabilities.get(i);
            }
        }
        if (probabilitySum > 0) {
            int probability = 0;
            int randomValue = randomInt(probabilitySum) + 1;
            for (int i = 0; i < probabilities.size(); i++) {
                if (exclusion == null || !exclusion.contains(i)) {
                    probability = probability + probabilities.get(i);
                    if (randomValue <= probability) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static <K> K randomOneKey(Map<K, Integer> map) {
        Map.Entry<K, Integer> entry = randomOne(map.entrySet(), Map.Entry::getValue);
        return entry == null ? null : entry.getKey();
    }
}
