package org.pot.game.engine.player.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;
@Setter
@Getter
public class PlayerFrame implements JsonObject, Serializable {
    @JsonProperty("frameId")
    private int frameId;

    @JsonProperty("time")
    private long time;
}
