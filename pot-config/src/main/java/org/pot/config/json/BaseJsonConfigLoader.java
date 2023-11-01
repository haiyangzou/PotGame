package org.pot.config.json;

import lombok.Setter;
import org.pot.common.file.loader.BaseLoader;
import org.pot.common.relect.ConstructorUtil;
import org.pot.config.Configure;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"rawtypes"})
abstract class BaseJsonConfigLoader implements BaseLoader {
    private static final AtomicInteger ORDINAL_GENERATOR = new AtomicInteger(Configure.DEFAULT_ORDINAL);
    protected final JsonConfigSupport jsonConfigSupport;
    protected final Class<? extends JsonConfig> jsonConfigClass;
    protected final JsonConfigSpec[] emptyConfigSpecArray;
    @Setter
    private volatile int ordinal = ORDINAL_GENERATOR.getAndIncrement();

    public BaseJsonConfigLoader(JsonConfigSupport jsonConfigSupport, Class<? extends JsonConfig> jsonConfigClass) throws Exception {
        this.jsonConfigSupport = jsonConfigSupport;
        this.jsonConfigClass = jsonConfigClass;
        JsonConfig jsonConfig = ConstructorUtil.newObjectWithNonParam(jsonConfigClass);
        this.emptyConfigSpecArray = (JsonConfigSpec[]) Array.newInstance(jsonConfig.getConfigSpecClass(), 0);
    }

    @Override
    public int ordinal() {
        return ordinal;
    }
}
