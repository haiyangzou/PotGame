package org.pot.common.file.watch;

import org.pot.common.file.FileType;

import java.io.File;

public abstract class FileTypeWatchListener extends FileWatchListener {
    protected final FileType fileType;

    public FileTypeWatchListener(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    protected boolean matches(File file) {
        return fileType.matches(file);
    }
}
