package org.pot.game.engine.player.component;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.event.EventBus;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.module.event.PlayerEvent;

@Slf4j
public class PlayerEventComponent {
    private final Player player;
    private final EventBus eventBus = new EventBus();

    public PlayerEventComponent(Player player) {
        this.player = player;
    }

    public void setThreadId() {
        eventBus.setThreadId();
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    public void handleAsyncEvent() {
        eventBus.handleAsyncEvent();
    }

    public <E extends PlayerEvent> void postPlayerEvent(final E event) {
        event.setPlayer(this.player);
        eventBus.post(event);
    }
}
