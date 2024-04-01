package org.pot.game.engine.player.module.hero;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PlayerHero {
    @JsonProperty("heroBaseId")
    private int heroBaseId;

    @JsonProperty("level")
    private int level;

    @JsonProperty("ability")
    private int ability;

    @JsonProperty("heroStatus")
    private volatile int heroStatus;

    @JsonProperty("color")
    private int color;

    @JsonProperty("strengthenLevel")
    private int strengthenLevel;

    @JsonProperty("star")
    private int star;

    @JsonProperty("rank")
    private int rank;

    @JsonProperty("skin")
    private String skin;

    @JsonProperty("equipId")
    private int equipId;

    @JsonProperty("backgroundReward")
    private int backgroundReward;

}
