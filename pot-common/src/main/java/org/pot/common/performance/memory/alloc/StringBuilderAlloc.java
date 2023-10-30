package org.pot.common.performance.memory.alloc;

public class StringBuilderAlloc {
    public static StringBuilder newString(final int numberOfItems) {
        return new StringBuilder(numberOfItems * 16);
    }

    public static StringBuilder newSmallString() {
        return newString(32);
    }
}
