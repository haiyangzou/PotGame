package org.pot.game.engine.player.module.resource;

import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.engine.reward.ResourceReward;
import org.pot.game.persistence.entity.PlayerResourceEntity;
import org.pot.game.resource.enums.ResourceType;
import org.pot.message.protocol.login.LoginRespS2C;
import org.pot.message.protocol.resource.ResourceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerResourceAgent extends PlayerAgentAdapter {
    private final PlayerResource playerResource = new PlayerResource();

    public PlayerResourceAgent(Player player) {
        super(player);
    }

    protected void initData(PlayerData playerData) {
        saveData(playerData);
    }

    protected void loadData(PlayerData playerData) {
        PlayerResourceEntity playerResource = playerData.getResourceEntity();
        Map<Integer, Long> resourceMap = playerResource.getResourceInfo();
        this.playerResource.setResourceMap(new ConcurrentHashMap<>(resourceMap));
    }

    protected void saveData(PlayerData playerData) {
        PlayerResourceEntity playerResourceEntity = new PlayerResourceEntity();
        playerResourceEntity.setId(this.player.getUid());
        playerResourceEntity.setResourceInfo(this.playerResource.getResourceMap());
        playerData.setResourceEntity(playerResourceEntity);
    }

    protected void onLogin(LoginRespS2C.Builder loginDataBuilder) {
        loginDataBuilder.addAllResources(getAllResource());
    }

    public List<ResourceInfo> getAllResource() {
        List<ResourceInfo> resourceInfos = new ArrayList<>();
        Map<Integer, Long> resourceMap = this.playerResource.getResourceMap();
        for (Map.Entry<Integer, Long> entry : resourceMap.entrySet()) {
            ResourceInfo.Builder builder = ResourceInfo.newBuilder();
            int type = entry.getKey();
            builder.setType(type);
            if (type == ResourceType.DIAMOND.getType()) {
                builder.setValue(getResource(ResourceType.DIAMOND).getCount());
            } else if (type == ResourceType.DIAMOND_INNER.getType() || type == ResourceType.DIAMOND_MIX.getType()) {
                builder.setValue(0L);
            } else {
                builder.setValue(entry.getValue());
            }
            resourceInfos.add(builder.build());
        }
        return resourceInfos;
    }

    public ResourceReward getResource(ResourceType resourceType) {
        if (PlayerResource.diamondResourceType.contains(resourceType)) {
            long l = this.playerResource.getDiamond();
            return new ResourceReward(ResourceType.DIAMOND, l);
        }
        long count = this.playerResource.getResource(resourceType.getType());
        return new ResourceReward(resourceType, count);
    }
}
