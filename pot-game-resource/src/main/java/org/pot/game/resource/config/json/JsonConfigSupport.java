package org.pot.game.resource.config.json;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.Constants;
import org.pot.common.util.FilenameUtil;

public class JsonConfigSupport {
    @Getter
    private final String baseDirPath;
    private static final String DEFAULT_CONFIG_PATH = "resource/";

    public JsonConfigSupport(String configRootPath) {
        String path = StringUtils.defaultIfBlank(configRootPath, DEFAULT_CONFIG_PATH);
        baseDirPath = FilenameUtil.formatPath(Constants.Env.getContextPath() + path);
    }

    public void load() {

    }
}
