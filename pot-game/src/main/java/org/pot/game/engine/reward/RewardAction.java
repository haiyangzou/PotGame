package org.pot.game.engine.reward;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.error.GameErrorCode;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class RewardAction {
    private boolean result = true;
    private GameErrorCode gameErrorCode = GameErrorCode.CONDITION_NOT_ENOUGH;
    private List<ResourceReward> resourceRewards = new ArrayList<>();
}
