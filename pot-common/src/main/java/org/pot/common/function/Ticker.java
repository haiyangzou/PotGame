package org.pot.common.function;

import org.pot.common.util.ClassUtil;

@FunctionalInterface
public interface Ticker {
    void tick();

    default String getTickerName() {
        return ClassUtil.getAbbreviatedName(this.getClass());
    }
}
