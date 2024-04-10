package org.pot.game.engine.player.module.hero;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

@Getter
@Setter
public class CardsPoolInfo implements Serializable, JsonObject {
    @JsonProperty("id")
    private int id;

    @JsonProperty("freeCount")
    private int freeCount;

    @JsonProperty("nextFreeTime")
    private long nextFreeTime;

    @JsonProperty("firstDraw")
    private boolean firstDraw;

}
