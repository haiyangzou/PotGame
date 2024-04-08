package org.pot.game.engine.player.module.resource;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.pot.game.resource.enums.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerResource {
    @Getter
    private Map<Integer, Long> resourceMap = new ConcurrentHashMap<>();

    public static final List<ResourceType> diamondResourceType = ImmutableList.of(ResourceType.DIAMOND, ResourceType.DIAMOND_INNER, ResourceType.DIAMOND_MIX);


    public void setResourceMap(Map<Integer, Long> resourceMap) {
        this.resourceMap = resourceMap;
    }

    public long getResource(int type) {
        return (this.resourceMap.getOrDefault(type, 0L));
    }

    public void calcUniversal(ResourceType resourceType, long calcCount) {
        if (diamondResourceType.contains(resourceType))
            return;
        this.resourceMap.merge(resourceType.getType(), calcCount, (old, val) -> Math.max(0L, old.longValue() + val.longValue()));
    }

    public synchronized long getDiamond() {
        long diamond = 0L;
        for (ResourceType resourceType : diamondResourceType)
            diamond += getResource(resourceType.getType());
        return diamond;
    }

    public synchronized void calcDiamond(ResourceType resourceType, long calcCount) {
        if (calcCount > 0L) {
            this.resourceMap.merge(resourceType.getType(), calcCount, (old, val) -> Math.max(0L, old + val));
        } else {
            long remaining = Math.abs(calcCount);
            for (ResourceType diamondType : diamondResourceType) {
                long diamond = getResource(diamondType.getType());
                if (remaining > diamond) {
                    remaining -= diamond;
                    this.resourceMap.merge(diamondType.getType(), diamond, (old, val) -> Math.max(0L, old - val));
                    continue;
                }
                this.resourceMap.merge(diamondType.getType(), remaining, (old, val) -> Math.max(0L, old - val));
            }
        }
    }
}
