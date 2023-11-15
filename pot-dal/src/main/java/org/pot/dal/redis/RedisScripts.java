package org.pot.dal.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.pot.common.clazz.ClassResourceUtil;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

@Slf4j
public class RedisScripts {
    public RedisScripts() {
    }

    public interface RedisScriptDef {
        String getName();

        String getPath();

        default Class<?> getResultType() {
            return null;
        }
    }

    private static final Map<String, RedisScript<?>> scriptMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> RedisScript<T> of(String filename) {
        return (RedisScript<T>) scriptMap.get(filename);
    }

    public static void load(RedisScriptDef[] redisScriptDef) throws IOException {
        for (RedisScriptDef scriptDef : redisScriptDef) {
            load(scriptDef);
        }
    }

    public static void load(RedisScriptDef redisScriptDef) throws IOException {
        load(redisScriptDef.getPath(), redisScriptDef.getPath(), redisScriptDef.getResultType());
    }

    public static void load(String scriptName, String scriptPath, Class<?> resultType) throws IOException {
        put(scriptName, ClassResourceUtil.loadText(scriptPath), resultType);
    }

    public static void load(String scriptPath, Class<?> resultType) throws IOException {
        load(scriptPath, scriptPath, resultType);
    }

    public static void load(URL directoryURL) throws IOException {
        switch (directoryURL.getProtocol()) {
            case "jar":
                JarURLConnection conn = (JarURLConnection) directoryURL.openConnection();
                JarFile jarFile = conn.getJarFile();
                jarFile.stream()
                        .forEach(jarEntry -> {
                            String entryName = jarEntry.getName();
                            if (!entryName.endsWith(".lua")) {
                                return;
                            }
                            String[] paths = entryName.split("/");
                            String fileName = paths[paths.length - 1];
                            try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarEntry))) {
                                put(fileName, IOUtils.toString(reader), null);
                            } catch (Throwable e) {
                                log.error("failed to getInputStream of JarEntry");
                            }
                        });
                break;
            case "file":
                Path path = null;
                try {
                    path = Paths.get(directoryURL.toURI());
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                            File file = filePath.toFile();
                            String fileName = file.getName();
                            if (fileName.endsWith(".lua")) {
                                try {
                                    put(fileName, FileUtils.readFileToString(file, StandardCharsets.UTF_8), null);
                                } catch (Throwable e) {
                                    log.error("failed to compile redis script");
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private static void put(String name, String text, Class<?> resultType) {
        scriptMap.computeIfAbsent(name, k -> resultType == null ? RedisScript.of(text) : RedisScript.of(text, resultType));
    }
}
