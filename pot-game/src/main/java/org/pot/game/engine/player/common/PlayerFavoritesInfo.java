package org.pot.game.engine.player.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class PlayerFavoritesInfo implements JsonObject, Serializable {
    @JsonProperty("favoritesSize")
    private int favoritesSize = 10;


    @JsonProperty("playerFavoritesDataMap")
    private Map<String, PlayerFavoritesData> playerFavoritesDataMap = new HashMap<>();
}
