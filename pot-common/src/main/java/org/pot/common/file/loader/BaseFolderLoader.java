package org.pot.common.file.loader;

import org.pot.common.file.FileType;

import java.io.File;
import java.io.IOException;

public interface BaseFolderLoader extends BaseLoader {
    @Override
    void onFileCreated(File file) throws IOException;

    @Override
    void onFileModified(File file);

    @Override
    void onFileDeleted(File file);

    @Override
    void loadAction(File file) throws Exception;

    FileType getFileType();

}
