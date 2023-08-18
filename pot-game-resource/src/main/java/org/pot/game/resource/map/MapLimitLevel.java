package org.pot.game.resource.map;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

public class MapLimitLevel extends InitJsonConfigSpec {
    @Getter
    private int serverDay;
    @Getter
    private int monsterLevel;
    @Getter
    private int rallyLevel;
}
