package org.pot.common.file.watch;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

public abstract class FileWatchListener extends FileAlterationListenerAdaptor {
    protected abstract boolean matches(File file);
}
