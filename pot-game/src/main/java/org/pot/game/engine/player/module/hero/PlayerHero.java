package org.pot.game.engine.player.module.hero;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.pot.game.engine.enums.HeroStatus;
import org.pot.game.resource.hero.HeroInfo;

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

    void initHero(HeroInfo heroInfo) {
        this.heroBaseId = heroInfo.getId();
        this.level = 1;
        this.ability = 0;
        this.heroStatus = HeroStatus.IDLE.value;
        this.color = heroInfo.getColor();
        this.strengthenLevel = 0;
        this.star = heroInfo.getStar();
        this.rank = 0;
        this.skin = "";
        this.equipId = 0;
        this.backgroundReward = 0;
    }

}
