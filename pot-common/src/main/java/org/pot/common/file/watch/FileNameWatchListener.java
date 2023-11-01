package org.pot.common.file.watch;

import java.io.File;

public class FileNameWatchListener extends FileWatchListener {
    protected final String fileName;

    public FileNameWatchListener(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected boolean matches(File file) {
        return fileName.equals(file.getName());
    }
}
