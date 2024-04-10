package org.pot.game.engine.player.module.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

@Getter
@Setter
public class ItemObject implements JsonObject, Serializable {
    @JsonProperty("uuid")
    private long uuid;

    @JsonProperty("prototype")
    private String prototype;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("endTime")
    private long endTime;

}
