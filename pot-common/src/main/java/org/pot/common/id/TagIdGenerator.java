package org.pot.common.id;

import lombok.Getter;

public class TagIdGenerator implements IdGenerator {
    private final long tagBits;
    private final long maxTagNum;
    @Getter
    private final long tag;
    private final TimeIdGenerator timeIdGenerator;

    public TagIdGenerator(long tagBits, long tag, TimeIdGenerator timeIdGenerator) {
        long max = MAX_ID_BITS_NUM - timeIdGenerator.getIdBitsNum();
        if (tagBits > max) {
            throw new IllegalArgumentException(String.format("tag bits %s is not allowed,max %s", tagBits, max));
        }
        this.tagBits = tagBits;
        this.maxTagNum = (1L << tagBits) - 1;
        if (tag < 1 || tag > maxTagNum) {
            throw new IllegalArgumentException(String.format("tag %s is not allowed", tag));
        }
        this.tag = tag;
        this.timeIdGenerator = timeIdGenerator;
    }

    public TagIdGenerator(long tag, TimeIdGenerator timeIdGenerator) {
        this(tag, MAX_ID_BITS_NUM - timeIdGenerator.getIdBitsNum(), timeIdGenerator);
    }

    @Override
    public long nextId() {
        final long timeBaseId = timeIdGenerator.nextId();
        final long id = (timeBaseId << tagBits) | tag;
        return id;
    }
}
