package org.pot.common.file.loader;

import org.pot.common.file.FileUtil;

import java.io.File;
import java.io.IOException;

public interface BaseFileLoader extends BaseLoader {
    @Override
    default void onFileCreated(File file) throws IOException {
        if (FileUtil.exists(file) && !file.isDirectory()) {
            LoadManager.addChangeLoader(this);
        }
    }

    @Override
    default void onFileModified(File file) {
        if (FileUtil.exists(file) && !file.isDirectory()) {
            LoadManager.addChangeLoader(this);
        }
    }

    @Override
    default void onFileDeleted(File file) {

    }

    @Override
    default void loadAction(File file) throws Exception {
        loadFile(file);
    }
}
