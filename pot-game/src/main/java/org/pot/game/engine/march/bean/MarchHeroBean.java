package org.pot.game.engine.march.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.pot.message.protocol.world.MarchHero;

@Getter
public class MarchHeroBean {
    @JsonProperty("heroId")
    private int heroId;

    public MarchHero buildProtoMessage() {
        MarchHero.Builder builder = MarchHero.newBuilder();
        builder.setHeroId(heroId);
        return builder.build();
    }

    public long getPower() {
        return 0;
    }
}
