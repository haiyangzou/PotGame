package org.pot.common.file.loader;

import java.io.File;
import java.io.IOException;

public interface BaseLoader extends Comparable<BaseLoader> {
    void onFileCreated(File file) throws IOException;

    void onFileModified(File file);

    void onFileDeleted(File file);

    void loadAction(File file) throws Exception;

    void loadFile(File file) throws Exception;

    int ordinal();

    @Override
    default int compareTo(BaseLoader o) {
        return Integer.compare(this.ordinal(), o.ordinal());
    }

    ;
}
