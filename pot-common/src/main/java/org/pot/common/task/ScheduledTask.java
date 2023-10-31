package org.pot.common.task;

@FunctionalInterface
public interface ScheduledTask {
    void execute();

    default boolean isAsync() {
        return false;
    }

    default String getScheduledTaskName() {
        return this.getClass().getName();
    }
}
