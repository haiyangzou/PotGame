package org.pot.game.engine.player.module.event;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.pot.game.engine.player.Player;

public abstract class PlayerEvent {
    @Setter
    @Getter
    @JsonIgnore
    private transient volatile Player player;
}
