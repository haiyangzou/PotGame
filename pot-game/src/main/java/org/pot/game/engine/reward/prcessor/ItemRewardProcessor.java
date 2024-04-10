package org.pot.game.engine.reward.prcessor;

import org.pot.game.engine.player.Player;
import org.pot.game.engine.reward.ResourceReward;
import org.pot.game.engine.reward.RewardAction;
import org.pot.game.engine.reward.RewardProcessor;
import org.pot.game.resource.common.Reward;

import java.util.List;

public class ItemRewardProcessor implements RewardProcessor {
    @Override
    public RewardAction check(Player paramPlayer, ResourceReward paramResourceReward) {
        return null;
    }

    @Override
    public List<ResourceReward> tryDiamond(Player paramPlayer, ResourceReward paramResourceReward) {
        return null;
    }

    @Override
    public ResourceReward execute(Player paramPlayer, ResourceReward paramResourceReward) {
        return null;
    }

    @Override
    public ResourceReward convertConditionInfo(Reward paramReward) {
        return null;
    }
}
