package org.pot.config.json;

import org.pot.common.file.FileType;
import org.pot.common.file.loader.BaseFolderLoader;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.file.TextFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FolderJsonConfigLoader extends BaseJsonConfigLoader implements BaseFolderLoader {
    private volatile List<JsonConfigSpec> loadingSpecList;

    public FolderJsonConfigLoader(JsonConfigSupport jsonConfigSupport, Class<? extends JsonConfig> jsonConfigClass) throws Exception {
        super(jsonConfigSupport, jsonConfigClass);
    }

    @Override
    public FileType getFileType() {
        return null;
    }

    @Override
    public void onFileCreated(File file) throws IOException {

    }

    @Override
    public void onFileModified(File file) {

    }

    @Override
    public void onFileDeleted(File file) {

    }

    @Override
    public void loadAction(File file) throws Exception {

    }

    @Override
    public void loadFile(File file) throws Exception {
        String json = TextFileUtil.read(file);
        JsonConfigSpec[] array = JsonUtil.parseJackJson(json, emptyConfigSpecArray.getClass());
        Arrays.stream(array).forEach(JsonConfigSpec::afterProperties);
        Collections.addAll(loadingSpecList, array);
    }

    @Override
    public int ordinal() {
        return 0;
    }
}
