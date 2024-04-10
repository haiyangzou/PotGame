package org.pot.game.engine.reward.resource;

import lombok.Getter;
import org.pot.game.engine.reward.ResourceReward;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;

@Getter
public class ItemResourceReward extends ResourceReward {
    private String itemId;

    public ItemResourceReward(Reward reward) {
        super(reward);
        this.itemId = reward.getId();
    }

    public ItemResourceReward(String itemId, int count) {
        super(ResourceType.ITEM, count);
        this.itemId = itemId;
    }
}
