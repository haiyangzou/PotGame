package org.pot.game.resource.map;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

public class SlgRallyInfo extends InitJsonConfigSpec {
    @Getter
    private int level;
    @Getter
    private int attrId;
    @Getter
    private int preId;
    @Getter
    private int strength;
}
