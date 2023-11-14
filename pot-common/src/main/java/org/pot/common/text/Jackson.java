package org.pot.common.text;

import org.pot.common.util.JsonUtil;

import java.util.Map;
import java.util.TreeMap;

public class Jackson {
    private final Map<String, Object> map;

    public Jackson(Map<String, Object> map) {
        this.map = map;
    }

    public Jackson(String json) {
        Map<String, Object> temp = JsonUtil.parseMap(json, new TreeMap<>(), String.class, Object.class);
        this.map = temp == null ? new TreeMap<>() : temp;
    }

    public final Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    public <T> T get(final String key, final Class<T> clazz) {
        Object value = this.map.get(key);
        if (value == null) return null;
        return JsonUtil.parseJson(JsonUtil.toJson(value), clazz);
    }
}
