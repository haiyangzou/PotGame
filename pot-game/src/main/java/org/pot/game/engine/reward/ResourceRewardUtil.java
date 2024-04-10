package org.pot.game.engine.reward;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.enums.RewardSourceType;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.reward.prcessor.*;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ResourceRewardUtil {
    private static final Map<ResourceType, RewardProcessor> handlerMap = new ConcurrentHashMap<>();

    static {
        handlerMap.put(ResourceType.FOOD, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.WOOD, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.STONE, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.MINE, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.HERO_EXP, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.POWER, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.ENERGY, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.EXP, new CommanderExpRewardProcessor());
        handlerMap.put(ResourceType.UNION_CURRENCY, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.RARE_EARTH_CURRENCY, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.DIAMOND, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.DIAMOND_INNER, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.DIAMOND_MIX, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.MARS_RESOURCE_A, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.MARS_RESOURCE_B, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.HERO_SCORE, new ResourceRewardProcessor());
        handlerMap.put(ResourceType.VIP_POINT, new VipPointRewardProcessor());
        handlerMap.put(ResourceType.HERO, new HeroRewardProcessor());
        handlerMap.put(ResourceType.ITEM, new ItemRewardProcessor());
    }

    public static RewardResult checkReward(Player player, boolean forDiamond, Reward... rewards) {
        ResourceReward[] convert = convert(rewards, RewardSourceType.COMMON);
        return checkResource(player, forDiamond, Arrays.asList(convert));
    }

    public static RewardResult checkResource(Player player, boolean forDiamond, List<ResourceReward> resourceRewards) {
        RewardResult rewardResult = new RewardResult();
        List<ResourceReward> resourceRewardList = new ArrayList<>();
        if (forDiamond) {
            List<ResourceReward> itemReward = ItemExchangeUtil.getItemReward(resourceRewards);
            if (itemReward != null && !itemReward.isEmpty()) {
                boolean checkHaveItem = ItemExchangeUtil.checkHaveItem(itemReward);
                if (!checkHaveItem) {
                    rewardResult.setResult(false);
                    rewardResult.setGameErrorCode(GameErrorCode.SHOP_ITEM_NOT_EXISTS);
                    return rewardResult;
                }
            }
            for (ResourceReward resourceReward : resourceRewards)
                resourceRewardList.addAll(tryDiamond(player, resourceReward));
        } else {
            resourceRewardList.addAll(resourceRewards);
        }
        ResourceReward[] combineResourceRewards = combineRewards(resourceRewardList.<ResourceReward>toArray(new ResourceReward[0]));
        for (ResourceReward resourceReward : combineResourceRewards) {
            RewardProcessor rewardProcessor = handlerMap.get(resourceReward.getResourceType());
            rewardResult.addRewardAction(rewardProcessor.check(player, resourceReward));
            if (!rewardResult.isResult())
                return rewardResult;
        }
        return rewardResult;
    }

    public static ResourceReward[] convert(Reward[] rewards, RewardSourceType sourceType) {
        List<ResourceReward> results = new ArrayList<>();
        if (rewards == null)
            return new ResourceReward[0];
        for (Reward reward : rewards) {
            if (reward != null) {
                ResourceReward resourceReward = convertSingle(reward);
                if (resourceReward != null)
                    results.add(resourceReward);
            }
        }
        if (!sourceType.isNeedCombine())
            return results.<ResourceReward>toArray(new ResourceReward[0]);
        return combineRewards(results.<ResourceReward>toArray(new ResourceReward[0]));
    }

    private static List<ResourceReward> tryDiamond(Player player, ResourceReward resourceReward) {
        RewardProcessor rewardProcessor = handlerMap.get(resourceReward.getResourceType());
        return rewardProcessor.tryDiamond(player, resourceReward);
    }

    public static ResourceReward convertSingle(Reward reward) {
        int type = reward.getType();
        ResourceType rewardType = ResourceType.getResourceType(type);
        if (rewardType == null)
            return null;
        RewardProcessor processor = handlerMap.get(rewardType);
        return processor.convertConditionInfo(reward);
    }

    public static IErrorCode checkAndExecute(Player player, RewardSourceType rewardSourceType, boolean forDiamond, Reward... rewards) {
        RewardResult rewardResult = checkReward(player, forDiamond, rewards);
        if (!rewardResult.isResult())
            return (rewardResult.getGameErrorCode() == null) ? GameErrorCode.CONDITION_NOT_ENOUGH : (IErrorCode) rewardResult.getGameErrorCode();
        executeResourceReward(player, rewardSourceType, rewardResult.getResourceRewards().<ResourceReward>toArray(new ResourceReward[0]));
        return null;
    }

    public static void executeResourceReward(Player player, RewardSourceType sourceType, ResourceReward... resourceRewards) {
        if (resourceRewards.length == 0)
            return;
        if (sourceType.isNeedCombine())
            resourceRewards = combineRewards(resourceRewards);
        List<ResourceReward> pushResourceReward = new ArrayList<>();
        for (ResourceReward resourceReward : resourceRewards) {
            RewardProcessor rewardProcessor = handlerMap.get(resourceReward.getResourceType());
            ResourceReward execute = rewardProcessor.execute(player, resourceReward);
            if (execute != null)
                pushResourceReward.add(execute);
        }
        player.resourceAgent.pushResources(pushResourceReward);
        player.itemAgent.pushItems();
        if (sourceType.getShowType() != null)
            player.resourceAgent.pushViewResources(Arrays.asList(resourceRewards), sourceType);
    }

    private static ResourceReward[] combineRewards(ResourceReward... resourceRewards) {
        Map<ResourceType, ResourceReward> rewardHashMap = new HashMap<>();
        List<ResourceReward> resourceRewardList = new ArrayList<>();
        for (ResourceReward resourceReward : resourceRewards) {
            if (resourceReward.getCount() != 0L)
                if (resourceReward.getResourceType() == ResourceType.ITEM || resourceReward.getResourceType() == ResourceType.SOLDIER) {
                    resourceRewardList.add(resourceReward);
                } else {
                    ResourceReward reward = rewardHashMap.get(resourceReward.getResourceType());
                    if (reward != null) {
                        reward.combine(resourceReward);
                    } else {
                        rewardHashMap.put(resourceReward.getResourceType(), resourceReward);
                    }
                }
        }
        resourceRewardList.addAll(new ArrayList<>(rewardHashMap.values()));
        return resourceRewardList.<ResourceReward>toArray(new ResourceReward[0]);
    }

    public static void executeReward(Player player, RewardSourceType sourceType, Reward... rewards) {
        if (rewards == null || rewards.length == 0)
            return;
        ResourceReward[] convert = convert(rewards, sourceType);
        executeResourceReward(player, sourceType, convert);
    }

    public static IErrorCode checkAndExecute(Player player, RewardSourceType rewardSourceType, boolean forDiamond, ResourceReward... resourceRewards) {
        RewardResult rewardResult = checkResource(player, forDiamond, Arrays.asList(resourceRewards));
        if (!rewardResult.isResult())
            return rewardResult.getGameErrorCode();
        executeResourceReward(player, rewardSourceType, rewardResult.getResourceRewards().<ResourceReward>toArray(new ResourceReward[0]));
        return null;
    }
}
