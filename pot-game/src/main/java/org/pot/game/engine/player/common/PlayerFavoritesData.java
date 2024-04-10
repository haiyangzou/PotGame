package org.pot.game.engine.player.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

@Setter
public class PlayerFavoritesData implements JsonObject, Serializable {
    @JsonProperty("relation")
    private int relation;

    @JsonProperty("name")
    private String name;

    @JsonProperty("serverId")
    private int serverId;

    @JsonProperty("x")
    private int x;

    @JsonProperty("y")
    private int y;
}
