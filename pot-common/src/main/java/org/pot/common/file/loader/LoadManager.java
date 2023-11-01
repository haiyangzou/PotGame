package org.pot.common.file.loader;

import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.file.FileUtil;
import org.pot.common.file.watch.FileNameWatchListener;
import org.pot.common.file.watch.FileTypeWatchListener;
import org.pot.common.file.watch.FileWatchService;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.FilenameUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public final class LoadManager extends Thread {
    private static final Map<BaseLoader, String> loaders = new ConcurrentHashMap<>();
    private static final BlockingQueue<BaseLoader> changedLoaders = new LinkedBlockingDeque<>();
    private static volatile long lastAddChangedLoaderTime = -1;
    private static volatile boolean changedLoaderAutoReload = false;
    private static volatile long changedLoaderLatchDurationTime = TimeUnit.SECONDS.toMillis(30);

    public LoadManager() {
        super(ClassUtil.getAbbreviatedName(LoadManager.class));
        this.setDaemon(true);
        this.start();
    }

    public static void startup(boolean changedLoaderAutoReload) throws Exception {
        LoadManager.changedLoaderAutoReload = changedLoaderAutoReload;
        FileWatchService.startup();
    }

    public static <T extends BaseLoader> void loadAndRegister(String filePath, T loader) {
        if (loader instanceof BaseFileLoader) {
            loadAndRegisterFile(filePath, (BaseFileLoader) loader);
        } else if (loader instanceof BaseFolderLoader) {
            loadAndRegisterFolder(filePath, (BaseFolderLoader) loader);
        } else {
            throw new IllegalArgumentException("Loader type error" + loader);
        }
    }

    public static <T extends BaseFolderLoader> void loadAndRegisterFolder(String filePath, T loader) {
        try {
            File file = new File(filePath);
            loader.loadAction(file);
            LoadManager.registerFolder(file, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static <T extends BaseFileLoader> void loadAndRegisterFile(String filePath, T loader) {
        try {
            File file = new File(filePath);
            loader.loadAction(file);
            LoadManager.registerFile(file, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void registerFile(File file, BaseFileLoader loader) {
        putLoader(file, loader);
        FileWatchService.register(file, new FileNameWatchListener(file.getName()) {

        });
    }

    public static void registerFolder(File folder, BaseFolderLoader loader) {
        putLoader(folder, loader);
        FileWatchService.register(folder, new FileTypeWatchListener(loader.getFileType()) {

        });
    }

    private static void putLoader(File file, BaseLoader loader) {
        String filePath = FilenameUtil.formatPath(file);
        String prevFilePath = loaders.put(loader, filePath);
        if (prevFilePath != null) {
            String error = String.format("loader %s already exists");
            throw new IllegalArgumentException(error);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                ThreadUtil.run(2000L, this::execute);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void execute() {
        if (isChangedLoaderAutoReload()) {
            reload(false);
        }
    }

    public static void reload(boolean force) {
        if (changedLoaders.isEmpty()) {
            return;
        }
        boolean reload = true;
        if (System.currentTimeMillis() - lastAddChangedLoaderTime < changedLoaderLatchDurationTime) {
            reload = false;
        }
        if (force) {
            reload = true;
        }
        if (!reload) {
            return;
        }
        BaseLoader baseLoader;
        ArrayList<BaseLoader> loaderList = new ArrayList<>();
        while ((baseLoader = changedLoaders.poll()) != null) {
            if (!loaderList.contains(baseLoader)) {
                loaderList.add(baseLoader);
            }
        }
        Collections.sort(loaderList);
        for (BaseLoader loader : loaderList) {
            reload(loader);
        }
    }

    private static void reload(BaseLoader loader) {
        if (loader == null) {
            return;
        }
        String filePath = loaders.get(loader);
        try {
            if (!FileUtil.exists(filePath)) {
                return;
            }
            loader.loadAction(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isChangedLoaderAutoReload() {
        return changedLoaderAutoReload;
    }

    static void addChangeLoader(BaseLoader baseLoader) {

    }
}
