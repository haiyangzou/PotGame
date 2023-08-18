package org.pot.game.resource.config.json;

import lombok.Getter;

import java.util.List;
import java.util.Map;

public abstract class JsonConfig<ID, T extends JsonConfigSpec<ID>> {
    @Getter
    private volatile Map<ID, T> specMap;
    @Getter
    private volatile List<T> specList;

    public T getSpec(ID id) {
        return specMap.get(id);
    }
}
