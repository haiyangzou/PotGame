package org.pot.game.resource.army;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

public class SoldierAttr extends InitJsonConfigSpec {
    @Getter
    private int type;
    @Getter
    private long ability;
    @Getter
    private int speed;
}
