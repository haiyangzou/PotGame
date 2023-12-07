package org.pot.game.engine.player.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.player.PlayerCaches;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.module.event.event.PlayerPowerChange;

@Slf4j
@Getter
public class PlayerPowerComponent {

    private final Player player;

    private volatile long power;

    public PlayerPowerComponent(Player player) {
        this.player = player;
    }

    public void calculatePowerAsync(boolean onlyCalculate) {
        this.player.submit(() -> calculatePower(onlyCalculate));
    }

    public long calculatePower(boolean onlyCalculate) {
        log.debug("Player Calculate Power Begin");
        long newPower = 0L;
        for (PlayerAgentAdapter playerAgent : this.player.agentAdapterList) {
            try {
                long agentPower = playerAgent.calculatePower();
                playerAgent.setPower(agentPower);
                newPower += playerAgent.getPower();
            } catch (Exception e) {
                log.error("Player Calculate Power Error! uid={}, Agent={}", this.player.getUid(), playerAgent.getClassSimpleName(), e);
            }
        }
        long oldPower = this.power;
        this.power = newPower;
        if (oldPower != newPower) {
            PlayerCaches.snapShot().updatePower(this.player.getUid(), newPower);
            if (!onlyCalculate)
                this.player.eventComponent.postPlayerEvent(PlayerPowerChange.builder().newPower(newPower).oldPower(oldPower).build());
        }
        return newPower;
    }

    public void agentPowerChange(PlayerAgentAdapter... agentAdapters) {
        for (PlayerAgentAdapter agentAdapter : agentAdapters) {
            long power = agentAdapter.calculatePower();
            agentAdapter.setPower(power);
        }
        long newPower = 0L;
        for (PlayerAgentAdapter playerAgent : this.player.agentAdapterList)
            newPower += playerAgent.getPower();
        long oldPower = this.power;
        this.power = newPower;
        if (oldPower != newPower) {
            PlayerCaches.snapShot().updatePower(this.player.getUid(), newPower);
            this.player.eventComponent.postPlayerEvent(PlayerPowerChange.builder().newPower(newPower).oldPower(oldPower).build());
        }
    }

    public void agentPowerChangeAsync(PlayerAgentAdapter... agentAdapters) {
        this.player.submit(() -> agentPowerChange(agentAdapters));
    }
}

