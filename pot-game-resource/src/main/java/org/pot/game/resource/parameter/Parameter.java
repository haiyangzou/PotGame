package org.pot.game.resource.parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pot.game.resource.StringJsonConfigSpec;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Parameter extends StringJsonConfigSpec {
    @Getter
    private String value;
}
