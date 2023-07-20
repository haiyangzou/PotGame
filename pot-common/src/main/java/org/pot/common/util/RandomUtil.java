package org.pot.common.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static long randomLong(long bound) {
        return ThreadLocalRandom.current().nextLong(bound);
    }

}
