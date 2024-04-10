package org.pot.game.engine.reward.resource;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.pot.game.engine.reward.ResourceReward;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class HeroResourceReward extends ResourceReward {
    private String heroId;

    public HeroResourceReward(Reward reward) {
        super(reward);
        this.heroId = reward.getId();
    }

    public HeroResourceReward(String heroId, int count) {
        super(ResourceType.HERO, count);
        this.heroId = heroId;
    }

    public void combine(ResourceReward resourceReward) {
        if (resourceReward instanceof HeroResourceReward) {
            HeroResourceReward heroResourceReward = (HeroResourceReward) resourceReward;
            String[] split = this.heroId.split(",");
            List<String> heroIds = new ArrayList<>(Arrays.asList(split));
            heroIds.add(heroResourceReward.getHeroId());
            this.heroId = StringUtils.join(heroIds, ",");
        }
    }
}
