package org.pot.game.resource.map;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

import java.util.Map;

public class RallyRefresh extends InitJsonConfigSpec {
    @Getter
    private int dayId;
    @Getter
    private int blockId;
    @Getter
    private int totalRate;
    @Getter
    private Map<Integer, Integer> rate;
}
