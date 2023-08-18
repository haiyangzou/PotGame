package org.pot.game.resource.map;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

import java.util.Map;

public class ResourceRefresh extends InitJsonConfigSpec {
    @Getter
    private Map<Integer, Integer> tile;
}
