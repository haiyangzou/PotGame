package org.pot.config.json;

import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class JsonConfig<ID, T extends JsonConfigSpec<ID>> {
    @Getter
    private final Class<T> configSpecClass;
    @Getter
    private volatile Map<ID, T> specMap;
    @Getter
    private volatile List<T> specList;

    public T getSpec(ID id) {
        return specMap.get(id);
    }

    public JsonConfig() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Actual Type Error:" + this.getClass().getName());
        }
        Class<T> temp = null;
        Type[] types = ((ParameterizedType) superClass).getActualTypeArguments();
        for (Type type : types) {
            if (type instanceof Class && JsonConfigSpec.class.isAssignableFrom((Class<?>) type)) {
                temp = (Class<T>) type;
                break;
            }
        }
        configSpecClass = temp;
        if (configSpecClass == null) {
            throw new IllegalStateException(this.getClass().getName() + "cannot get spec class");
        }
    }

    void setSpecMap(Map<ID, T> specMap) {
        this.specMap = specMap;
    }

    void setSpecList(List<T> specList) {
        this.specList = specList;
    }

    protected void afterProperties() {

    }
}
