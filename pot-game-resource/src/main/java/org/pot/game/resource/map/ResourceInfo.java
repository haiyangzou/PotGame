package org.pot.game.resource.map;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

public class ResourceInfo extends InitJsonConfigSpec {
    @Getter
    private long amount;
}
