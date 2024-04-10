package org.pot.game.engine.reward;

import lombok.Getter;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;

@Getter
public class ResourceReward {
    protected ResourceType resourceType;

    protected long count;

    public ResourceReward(ResourceType resourceType, long count) {
        this.resourceType = resourceType;
        this.count = count;
    }

    public ResourceReward(Reward reward) {
        this.count = reward.getCount();
        int type = reward.getType();
        this.resourceType = ResourceType.getResourceType(type);
    }

    public void combine(ResourceReward resourceReward) {
        this.count += resourceReward.getCount();
    }
}
