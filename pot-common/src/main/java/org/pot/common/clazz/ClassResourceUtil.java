package org.pot.common.clazz;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClassResourceUtil {
    public static String loadText(String filePath) throws IOException {
        return loadText(Thread.currentThread().getContextClassLoader(), filePath);
    }

    public static String loadText(ClassLoader classLoader, String filePath) throws IOException {
        String result = StringUtils.EMPTY;
        InputStream inputStream = getInputStream(classLoader, filePath);
        if (inputStream != null) {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            IOUtils.closeQuietly(inputStream, Throwable::printStackTrace);
        }
        return result;
    }

    public static InputStream getInputStream(String filePath) {
        return getInputStream(Thread.currentThread().getContextClassLoader(), filePath);
    }

    public static InputStream getInputStream(ClassLoader classLoader, String filePath) {
        return classLoader.getResourceAsStream(filePath);
    }
}
