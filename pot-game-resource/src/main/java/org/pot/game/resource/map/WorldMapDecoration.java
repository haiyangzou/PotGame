package org.pot.game.resource.map;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

public class WorldMapDecoration extends InitJsonConfigSpec {
    @Getter
    private int iRange;
    @Getter
    private int jRange;

    @Getter
    private boolean canBuild;
}
