package org.pot.common.id;

public interface IntIdGenerator {
    int MAX_BITS = 31;

    int nextId();
}
