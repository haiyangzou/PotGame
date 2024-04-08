package org.pot.game.engine.reward;

import lombok.Getter;
import org.pot.game.resource.enums.ResourceType;

@Getter
public class ResourceReward {
    protected ResourceType resourceType;

    protected long count;

    public ResourceReward(ResourceType resourceType, long count) {
        this.resourceType = resourceType;
        this.count = count;
    }
}
