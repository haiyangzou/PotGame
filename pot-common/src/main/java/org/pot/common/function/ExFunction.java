package org.pot.common.function;

@FunctionalInterface
public interface ExFunction<T, R> {
    R apply(T t) throws Exception;

    default String getExFunctionName() {
        return this.getClass().getName();
    }
}
