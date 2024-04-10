package org.pot.game.resource.shop;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;
import org.pot.game.resource.common.Condition;
import org.pot.game.resource.common.Reward;
@Getter
public class ExchangeShop extends InitJsonConfigSpec {
    private int shopType;

    private Reward item;

    private Reward price;

    private Condition limit;

    private int limitCount;

    private int refreshType;

    private long refreshTime;

    private int buyLimit;
}
