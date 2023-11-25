package org.pot.game.engine.player.module.army;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

public class PlayerArmy implements JsonObject, Serializable {
    @Getter
    @JsonProperty("armyId")
    private int armyId;
    @JsonProperty("count")
    private volatile long count;

}
