package org.pot.hotswap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.date.DateTimeString;
import org.pot.common.util.FilenameUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 采用premain形式支持热更
 * 依赖本包之后，添加启动参数-javaagent:lib/hotswap.jar
 */
@Slf4j
public class HotSwapAgent {
    private static final String NewLine = System.getProperty("line.separator");
    private static volatile boolean running = false;

    public HotSwapAgent() {
    }

    public static String hotSwap(final String classFileFolderPath, final ClassLoader classLoader) {
        log.info("[HOTSWAP] path:{}", classFileFolderPath);
        StringBuilder builder = new StringBuilder();
        long begin = System.currentTimeMillis();
        builder.append("[HOTSWAP] date: ").append(DateTimeString.Date_TIME_MILLIS_ZONE.toString(begin));
        builder.append("[HOTSWAP] path: ").append(classFileFolderPath).append(NewLine);
        if (HotSwapPremain.getInstrumentation() == null) {
            String message = "[HOTSWAP] [ERROR] please run application use jvm option [-javaagent:libs/hotswap.jar] ";
            log.error(message);
            return builder.append(message).toString();
        }
        File classFileFolder = new File(classFileFolderPath);
        if (!classFileFolder.exists()) {
            String message = "[HOTSWAP] [ERROR] class file folder not exists:  " + classFileFolderPath;
            log.error(message);
            return builder.append(message).toString();
        }
        if (running) {
            String message = "[HOTSWAP] [ERROR] please waiting for last time completed";
            log.error(message);
            return builder.append(message).toString();
        }
        try {
            running = true;
            Map<String, String> classFileMap = prepareClassFileMap(classFileFolder);
            if (classFileMap.isEmpty()) {
                String message = "[HOTSWAP] [ERROR] not found any class file";
                log.error(message);
                return builder.append(message).toString();
            }
            for (Map.Entry<String, String> entry : classFileMap.entrySet()) {
                String className = entry.getKey();
                String classPath = entry.getValue();
                String redefine = redefineClass(classLoader, className, classPath);
                builder.append(redefine).append(NewLine);
            }
            long used = System.currentTimeMillis() - begin;
            String message = "[HOTSWAP] used:" + used + "ms";
            return builder.append(message).toString();
        } catch (Throwable cause) {
            String message = "[HOTSWAP] [ERROR] caused by " + cause;
            log.error(message);
            return builder.append(message).toString();
        } finally {
            running = false;
        }
    }

    private static byte[] readFileBytes(final File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int readLength;
            byte[] buffer = new byte[0x2000];
            while ((readLength = fileInputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, readLength);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static String redefineClass(final ClassLoader classLoader, final String className, final String classPath) {
        String message;
        try {
            File classFile = new File(classPath);
            final Class<?> clazz = ClassUtils.getClass(classLoader, className);
            final byte[] code = readFileBytes(classFile);
            final Instrumentation inst = HotSwapPremain.getInstrumentation();
            inst.redefineClasses(new ClassDefinition(clazz, code));
            message = "[HOTSWAP] " + clazz.getName() + "redefined";
        } catch (Throwable cause) {
            message = "[HOTSWAP] [ERROR] " + className + "caused by" + cause;
            log.error(message);
        }
        return message;
    }

    private static Map<String, String> prepareClassFileMap(final File classFileFolder) {
        Map<String, String> map = new LinkedHashMap<>();
        String classFileFolderPath = FilenameUtil.formatPath(classFileFolder);
        Collection<File> classFiles = FileUtils.listFiles(classFileFolder, new String[]{"class"}, true);
        for (File classFile : classFiles) {
            String classFilePath = FilenameUtil.formatPath(classFile);
            String className = StringUtils.removeStart(classFilePath, classFileFolderPath);
            className = StringUtils.removeEnd(className, "class");
            className = StringUtils.replace(className, File.separator, ".");
            className = StringUtils.strip(className, ".");
            map.put(className, classFilePath);
        }
        return map;
    }

}
