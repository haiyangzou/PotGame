package org.pot.game.resource.recruit;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;
@Configure(file = "CardsDraw.json")
public class CardsDrawConfig extends InitJsonConfig<CardsDraw> {

    public static CardsDrawConfig getInstance() {
        return GameConfigSupport.getConfig(CardsDrawConfig.class);
    }
}
