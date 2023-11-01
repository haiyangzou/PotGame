package org.pot.common.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class FilenameUtil {
    public static String formatPath(File file) {
        String absolutePath = file.getAbsolutePath();
        String result = FilenameUtils.normalize(absolutePath);
        result = FilenameUtils.separatorsToUnix(result);
        if (file.isDirectory()) {
            result = StringUtils.endsWith(result, File.separator) ? result : result + File.separator;
        }
        return FilenameUtils.separatorsToUnix(result);
    }

    public static String formatPath(String file) {
        if (StringUtils.isBlank(file)) {
            return file;
        }
        return formatPath(new File(file));
    }

    public static boolean isSubversionFile(File file) {
        if (file == null) {
            return false;
        }
        String fullFileName = FilenameUtils.separatorsToSystem(FilenameUtils.normalize(file.getAbsolutePath()));
        return StringUtils.contains(fullFileName, ".svn");
    }

}
