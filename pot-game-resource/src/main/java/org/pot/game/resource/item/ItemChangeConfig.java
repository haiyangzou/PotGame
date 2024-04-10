package org.pot.game.resource.item;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.StringJsonConfig;

@Configure(file = "ItemChange.json")
public class ItemChangeConfig extends StringJsonConfig<ItemChange> {
    public static ItemChangeConfig getInstance() {
        return GameConfigSupport.getConfig(ItemChangeConfig.class);
    }
}