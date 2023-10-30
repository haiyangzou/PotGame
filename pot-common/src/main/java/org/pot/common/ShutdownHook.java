package org.pot.common;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ShutdownHook extends Thread {
    private static final ShutdownHook instance;
    private static final int defaultPriority = 20;

    static {
        instance = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(instance);
    }

    public static void registerShutdownHook(AutoCloseable closeable) {
        registerShutdownHook(closeable, defaultPriority);
    }

    public static void registerShutdownHook(AutoCloseable closeable, int priority) {
        instance.register(closeable, priority);
    }

    private static class CloseableObject implements Comparable<CloseableObject> {
        AutoCloseable closeable;
        int priority;

        public CloseableObject(AutoCloseable closeable, int priority) {
            this.closeable = closeable;
            this.priority = priority;
        }

        @Override
        public int compareTo(CloseableObject o) {
            return Long.compare(this.priority, o.priority);
        }
    }

    @Override
    public void run() {
        synchronized (resourceList) {
            Collections.sort(resourceList);
            for (CloseableObject resource : resourceList) {
                try {
                    resource.closeable.close();
                } catch (Exception e) {
                    log.error("Failed to execute shutdown hook" + resource.closeable.getClass().getSimpleName(), e);
                }
            }
            resourceList.clear();
        }
    }

    private final List<CloseableObject> resourceList = new ArrayList<>();

    private void register(AutoCloseable closeable, int priority) {
        if (closeable == null) {
            return;
        }
        synchronized (resourceList) {

        }
    }
}
