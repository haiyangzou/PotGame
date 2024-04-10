package org.pot.game.engine.reward;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.enums.ShopTypeEnum;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.reward.resource.ItemResourceReward;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;
import org.pot.game.resource.shop.ExchangeShop;
import org.pot.game.resource.shop.ExchangeShopConfig;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ItemExchangeUtil {

    public static long itemForDiamond(Player player, List<ItemResourceReward> rewards, List<ItemResourceReward> resourceRewards) {
        long totalDiamond = 0L;
        for (ItemResourceReward reward : rewards) {
            String itemId = reward.getItemId();
            int itemCount = player.itemAgent.getItemCount(itemId);
            ExchangeShop exchangeShop = ExchangeShopConfig.getInstance().getGoodsId(ShopTypeEnum.DIAMOND.getType(), itemId);
            if (exchangeShop == null)
                exchangeShop = ExchangeShopConfig.getInstance().getGoodsId(ShopTypeEnum.DIAMOND.getType(), itemId);
            Reward cost = exchangeShop.getPrice();
            long abs = Math.abs(itemCount + reward.getCount());
            totalDiamond += cost.getCount() * abs;
            resourceRewards.add(new ItemResourceReward(itemId, (int) abs));
        }
        return totalDiamond;
    }

    public static boolean checkHaveItem(List<ResourceReward> resourceRewards) {
        for (ResourceReward resourceReward : resourceRewards) {
            ItemResourceReward itemResourceReward = (ItemResourceReward) resourceReward;
            String itemId = itemResourceReward.getItemId();
            ExchangeShop exchangeShop = ExchangeShopConfig.getInstance().getGoodsId(ShopTypeEnum.DIAMOND.getType(), itemId);
            if (exchangeShop == null) {
                exchangeShop = ExchangeShopConfig.getInstance().getGoodsId(ShopTypeEnum.DIAMOND.getType(), itemId);
                if (exchangeShop == null) {
                    log.error("exchange item :{} not exist", itemId);
                    return false;
                }
            }
        }
        return true;
    }

    public static List<ResourceReward> getItemReward(List<ResourceReward> rewards) {
        return rewards.stream().filter(reward -> (reward.getResourceType() == ResourceType.ITEM)).collect(Collectors.toList());
    }
}
