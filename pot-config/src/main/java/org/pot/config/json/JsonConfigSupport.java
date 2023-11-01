package org.pot.config.json;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.Constants;
import org.pot.common.PotPackage;
import org.pot.common.file.loader.LoadManager;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.FilenameUtil;
import org.pot.config.Configure;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JsonConfigSupport {
    @Getter
    private final String baseDirPath;
    private static final String DEFAULT_CONFIG_PATH = "resource/";
    private final Map<Class<? extends JsonConfig>, JsonConfig> configMap = new ConcurrentHashMap<>();

    public JsonConfigSupport(String configRootPath) {
        String path = StringUtils.defaultIfBlank(configRootPath, DEFAULT_CONFIG_PATH);
        baseDirPath = FilenameUtil.formatPath(Constants.Env.getContextPath() + path);
        File baseDir = new File(baseDirPath);
        if (!baseDir.exists()) {
            throw new IllegalArgumentException("Json Config Path Not Exists:" + baseDirPath);
        }
        if (!baseDir.isDirectory()) {
            throw new IllegalArgumentException("Json Config Path not Directory:" + baseDirPath);
        }
    }

    public void load() {
        try {
            Set<Class<? extends JsonConfig>> configClassSet = ClassUtil.getSubTypeOf(
                    PotPackage.class, JsonConfig.class, Configure.class, ClassUtil::isConcrete);
            List<Class<? extends JsonConfig>> list = new ArrayList<>(configClassSet);
            list.sort(Comparator.comparing(o -> o.getAnnotation(Configure.class).ordinal()));
            for (Class<? extends JsonConfig> configClass : list) {
                Configure configure = configClass.getAnnotation(Configure.class);
                String configFilePath = baseDirPath + configure.file();
                File configFile = new File(configFilePath);
                if (!configFile.exists()) {
                    continue;
                }
                BaseJsonConfigLoader loader = configFile.isDirectory()
                        ? new FolderJsonConfigLoader(this, configClass)
                        : new FileJsonConfigLoader(this, configClass);
                if (configure.ordinal() != Configure.DEFAULT_ORDINAL) {
                    loader.setOrdinal(configure.ordinal());
                }
                LoadManager.loadAndRegister(configFilePath, loader);
            }
            LoadManager.startup(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void putJsonConfig(Class<? extends JsonConfig> jsonConfigClass, JsonConfig jsonConfig) {
        configMap.put(jsonConfigClass, jsonConfig);
    }

    public <T> T getJsonConfig(Class<T> configClass) {
        return (T) configMap.get(configClass);
    }
}
