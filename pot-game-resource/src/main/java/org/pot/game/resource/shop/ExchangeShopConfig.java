package org.pot.game.resource.shop;

import org.pot.common.util.MapUtil;
import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configure(file = "ExchangeShop.json")
public class ExchangeShopConfig extends InitJsonConfig<ExchangeShop> {
    public static ExchangeShopConfig getInstance() {
        return GameConfigSupport.getConfig(ExchangeShopConfig.class);
    }

    private volatile Map<Integer, Map<String, ExchangeShop>> exchangeShopInfoMap = Collections.emptyMap();

    protected void afterProperties() {
        Map<Integer, Map<String, ExchangeShop>> map = new HashMap<>(getSpecList().size());
        for (ExchangeShop spec : getSpecList()) {
            if (spec.getItem().getType() != ResourceType.ITEM.getType())
                continue;
            map.computeIfAbsent(spec.getShopType(), k -> new HashMap<>()).put(spec.getItem().getId(), spec);
        }
        this.exchangeShopInfoMap = MapUtil.immutableMapMap(map);
    }

    public ExchangeShop getGoodsId(int shopType, String itemId) {
        Map<String, ExchangeShop> exchangeShopMap = this.exchangeShopInfoMap.get(shopType);
        return (exchangeShopMap == null) ? null : exchangeShopMap.get(itemId);
    }

    protected void checkConfig() {
        for (ExchangeShop exchangeShop : getSpecList()) {
            Reward priceReward = exchangeShop.getPrice();
            if (priceReward.getCount() > 0L)
                continue;
            if (priceReward.getType() == ResourceType.ITEM.getType() && exchangeShop.getBuyLimit() > 2147483647L / -priceReward.getCount())
                continue;
            if (exchangeShop.getBuyLimit() > Long.MAX_VALUE / -priceReward.getCount()) ;
        }
    }
}
