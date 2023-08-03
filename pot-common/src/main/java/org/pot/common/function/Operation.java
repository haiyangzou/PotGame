package org.pot.common.function;

import org.pot.common.util.ClassUtil;

@FunctionalInterface
public interface Operation {
    void operate();

    default String getOperationName() {
        return ClassUtil.getAbbreviatedName(this.getClass());
    }
}
