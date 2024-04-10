package org.pot.game.resource.drop;

import org.pot.common.util.MapUtil;
import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

import java.util.*;

@Configure(file = "DropInfo.json")
public class DropInfoConfig extends InitJsonConfig<DropInfo> {
    public static DropInfoConfig getInstance() {
        return GameConfigSupport.getConfig(DropInfoConfig.class);
    }

    private volatile Map<Integer, List<DropInfo>> dropInfoMap = Collections.emptyMap();

    protected void afterProperties() {
        Map<Integer, List<DropInfo>> map = new HashMap<>();
        for (DropInfo spec : getSpecList())
            ((List<DropInfo>) map.computeIfAbsent(Integer.valueOf(spec.getDropNo()), k -> new ArrayList())).add(spec);
        this.dropInfoMap = MapUtil.immutableMapList(map);
    }

    public List<DropInfo> getDropInfoList(int dropNo) {
        return this.dropInfoMap.getOrDefault(Integer.valueOf(dropNo), new ArrayList<>());
    }
}
