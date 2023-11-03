package org.pot.game.engine.march.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MarchHeroBean {
    @JsonProperty("heroId")
    private int heroId;
}
