package org.pot.game.resource.item;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.resource.StringJsonConfigSpec;

@Getter
@Setter
public class ItemChange extends StringJsonConfigSpec {
    private String changeId;

    private int changePercent;
}
