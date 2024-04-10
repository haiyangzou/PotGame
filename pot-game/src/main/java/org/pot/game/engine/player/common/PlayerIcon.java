package org.pot.game.engine.player.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;
@Setter
@Getter
public class PlayerIcon implements JsonObject, Serializable {
    @JsonProperty("iconId")
    private int iconId;

    @JsonProperty("time")
    private long time;
}
