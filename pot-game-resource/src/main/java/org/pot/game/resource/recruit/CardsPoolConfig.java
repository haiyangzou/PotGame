package org.pot.game.resource.recruit;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "CardsPool.json")
public class CardsPoolConfig extends InitJsonConfig<CardsPool> {
    public static CardsPoolConfig getInstance() {
        return GameConfigSupport.getConfig(CardsPoolConfig.class);
    }
}
