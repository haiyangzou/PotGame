package org.pot.common.file;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;

public enum FileType implements FilenameFilter {
    ANY(StringUtils.EMPTY) {
        @Override
        public String getPointAndExtension() {
            return StringUtils.EMPTY;
        }

        @Override
        public String getPatternString() {
            return "^.*$";
        }
    },
    UI("ui"),
    SH("sh"),
    ;
    final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File dir, String name) {
        return false;
    }

    public boolean matches(String fileNameOrFilePath) {
        return Objects.nonNull(fileNameOrFilePath) && fileNameOrFilePath.toLowerCase().endsWith(getPointAndExtension());
    }

    public String getExtension() {
        return extension;
    }

    public String getPointAndExtension() {
        return '.' + getExtension();
    }

    public boolean matches(File file) {
        return file != null && matches(file.getName());
    }

    public String getPatternString() {
        return "^.*\\." + getExtension() + "$";
    }
}
