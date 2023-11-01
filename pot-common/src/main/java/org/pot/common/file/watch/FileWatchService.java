package org.pot.common.file.watch;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.util.FilenameUtil;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FileWatchService {
    private static final FileAlterationMonitor monitor = new FileAlterationMonitor(2000L);
    private static final Map<String, FileAlterationObserver> observers = new ConcurrentHashMap<>();

    static {
        monitor.setThreadFactory(ThreadUtil.newThreadFactory("FileMonitorServer-%d", true));
    }

    public static void startup() throws Exception {
        monitor.start();
    }

    public static void shutdown() throws Exception {
        monitor.stop();
        observers.clear();
    }

    public static void register(String file, FileWatchListener listener) {
        register(new File(file), listener);
    }

    public static void register(File file, FileWatchListener listener) {
        if (FilenameUtil.isSubversionFile(file)) {
            return;
        }
        String path;
        if (file.isDirectory()) {
            path = file.getAbsolutePath();
        } else {
            path = file.getParentFile().getAbsolutePath();
        }
        path = FilenameUtil.formatPath(path);
        FileAlterationObserver observer = observers.computeIfAbsent(path, k -> {
            FileAlterationObserver temp = new FileAlterationObserver(k, null, IOCase.SENSITIVE);
            monitor.addObserver(temp);
            return temp;
        });
        observer.addListener(listener);
    }
}
