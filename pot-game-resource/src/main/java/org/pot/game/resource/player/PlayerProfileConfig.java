package org.pot.game.resource.player;


import org.pot.common.util.MapUtil;
import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

import java.util.stream.Collectors;
import java.util.*;

@Configure(file = "PlayerProfile.json")
public class PlayerProfileConfig extends InitJsonConfig<PlayerProfile> {
    public static PlayerProfileConfig getInstance() {
        return GameConfigSupport.getConfig(PlayerProfileConfig.class);
    }

    private volatile Map<Integer, List<PlayerProfile>> radarEventMap = Collections.emptyMap();

    protected void afterProperties() {
        Map<Integer, List<PlayerProfile>> map = new HashMap<>();
        for (PlayerProfile spec : getSpecList())
            (map.computeIfAbsent(spec.getUnlockValue(), k -> new ArrayList<>())).add(spec);
        this.radarEventMap = MapUtil.immutableMapList(map);
    }

    public List<PlayerProfile> getListByValue(int unlockValue, int unlockType) {
        List<PlayerProfile> profileList = this.radarEventMap.getOrDefault(unlockValue, new ArrayList<>());
        return profileList.stream().filter(playerProfile -> (playerProfile.getUnlockType() == unlockType)).collect(Collectors.toList());
    }
}