package org.pot.game.resource.config.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"id"})
public abstract class JsonConfigSpec<ID> {
    @Getter
    private ID id;

    protected void afterProperties() {

    }
}
