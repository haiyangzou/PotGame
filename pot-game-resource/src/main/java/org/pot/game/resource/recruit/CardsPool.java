package org.pot.game.resource.recruit;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;
@Getter
public class CardsPool extends InitJsonConfigSpec {
    private int freeCount;

    private long freeTime;

    private int firstFree;

    private int freeRefreshType;
}
