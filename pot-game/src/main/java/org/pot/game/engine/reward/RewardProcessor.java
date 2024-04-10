package org.pot.game.engine.reward;

import org.pot.game.engine.player.Player;
import org.pot.game.resource.common.Reward;

import java.util.List;

public interface RewardProcessor {
    RewardAction check(Player paramPlayer, ResourceReward paramResourceReward);

    List<ResourceReward> tryDiamond(Player paramPlayer, ResourceReward paramResourceReward);

    ResourceReward execute(Player paramPlayer, ResourceReward paramResourceReward);

    ResourceReward convertConditionInfo(Reward paramReward);
}
