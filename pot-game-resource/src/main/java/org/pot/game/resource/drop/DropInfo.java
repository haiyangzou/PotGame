package org.pot.game.resource.drop;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;
import org.pot.game.resource.common.Reward;
@Getter
public class DropInfo extends InitJsonConfigSpec {
    private Reward reward;

    private int rate;

    private int totalRate;

    private int maxGlobalCount;

    private int maxPersonalCount;

    private int isDefault;

    private int noticeId;

    private int minLevel;

    private int maxLevel;

    private int minCastleLevel;

    private int maxCastleLevel;

    private int minRadarLevel;

    private int timePeriod;

    private int dropNo;
}
