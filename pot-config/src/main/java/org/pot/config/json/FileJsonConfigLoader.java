package org.pot.config.json;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.pot.common.file.loader.BaseFileLoader;
import org.pot.common.relect.ConstructorUtil;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.MapUtil;
import org.pot.common.util.file.TextFileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class FileJsonConfigLoader extends BaseJsonConfigLoader implements BaseFileLoader {
    public FileJsonConfigLoader(JsonConfigSupport jsonConfigSupport, Class<? extends JsonConfig> jsonConfigClass) throws Exception {
        super(jsonConfigSupport, jsonConfigClass);
    }

    @Override
    public void loadFile(File file) throws Exception {
        String json = TextFileUtil.read(file, false);
        JsonConfigSpec[] array = JsonUtil.parseJackJson(json, emptyConfigSpecArray.getClass());
        Arrays.stream(array).forEach(JsonConfigSpec::afterProperties);
        ImmutableList<JsonConfigSpec> specsList = ImmutableList.copyOf(array);
        Map<Object, JsonConfigSpec> temp = MapUtil.toLinkedHashMap(specsList, JsonConfigSpec::getId);
        ImmutableMap<Object, JsonConfigSpec> specMap = ImmutableMap.copyOf(temp);
        JsonConfig jsonConfig = ConstructorUtil.newObjectWithNonParam(jsonConfigClass);
        jsonConfig.setSpecList(specsList);
        jsonConfig.setSpecMap(specMap);
        jsonConfig.afterProperties();
        jsonConfigSupport.putJsonConfig(jsonConfigClass, jsonConfig);
    }
}
