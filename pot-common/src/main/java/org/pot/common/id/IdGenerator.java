package org.pot.common.id;

public interface IdGenerator {
    long MAX_ID_BITS_NUM = 63;

    long nextId();
}
