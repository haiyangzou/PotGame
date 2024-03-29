package org.pot.game.resource.clean;


import lombok.Getter;
import org.pot.config.Configure;

import java.util.Collections;
import java.util.List;

@Configure(file = "CleanPlayerRule.json")
public class CleanPlayerRuleConfig {
    public CleanPlayerRuleConfig() {
    }

    @Getter
    private volatile List<CleanPlayerRule> cleanPlayerRules = Collections.emptyList();

    public static CleanPlayerRuleConfig getInstance() {
        return new CleanPlayerRuleConfig();
    }

    public List<CleanPlayerRule> getSpecList() {
        return null;
    }

    public CleanPlayerRule getSpec(String id) {
        return null;
    }

}
