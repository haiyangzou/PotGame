package org.pot.game.engine.reward;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.error.GameErrorCode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RewardResult {
    private boolean result = true;
    private List<ResourceReward> resourceRewards = new ArrayList<>();
    private GameErrorCode gameErrorCode = GameErrorCode.CONDITION_NOT_ENOUGH;

    public void addRewardAction(RewardAction rewardAction) {
        this.result = (this.result && rewardAction.isResult());
        this.gameErrorCode = rewardAction.getGameErrorCode();
        addResourceReward(rewardAction.getResourceRewards());
    }

    public void addRewardResult(RewardResult rewardResult) {
        this.result = (this.result && rewardResult.isResult());
        this.gameErrorCode = rewardResult.getGameErrorCode();
        addResourceReward(rewardResult.getResourceRewards());
    }

    public void addResourceReward(List<ResourceReward> resourceRewardList) {
        this.resourceRewards.addAll(resourceRewardList);
    }
}
