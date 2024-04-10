package org.pot.game.resource.hero;

import com.google.common.collect.ImmutableMap;
import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configure(file = "HeroInfo.json")
public class HeroInfoConfig extends InitJsonConfig<HeroInfo> {
    public static HeroInfoConfig getInstance() {
        return GameConfigSupport.getConfig(HeroInfoConfig.class);
    }

    private volatile Map<String, HeroInfo> heroInfoMedalMap = Collections.emptyMap();

    protected void afterProperties() {
        Map<String, HeroInfo> map = new HashMap<>();
        for (HeroInfo spec : getSpecList()) {
            if (spec.getMedalId() != null)
                map.put(spec.getMedalId(), spec);
        }
        this.heroInfoMedalMap = ImmutableMap.copyOf(map);
    }

    public HeroInfo getHeroInfoByMedalId(String medalId) {
        return this.heroInfoMedalMap.get(medalId);
    }
}
