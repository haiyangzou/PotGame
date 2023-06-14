package org.pot.common.util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

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
    CLASS("class");

    final String extension;

    FileType(String endString) {
        this.extension = endString;
    };

    @Override
    public boolean accept(File dir, String name) {

        return true;
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

    public String getPatternString() {
        return "^.*\\." + getExtension() + "$";
    }
}
