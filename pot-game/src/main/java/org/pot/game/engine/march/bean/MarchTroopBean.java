package org.pot.game.engine.march.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.databind.json.JsonObject;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.march.MarchUtil;
import org.pot.game.engine.player.Player;

import java.util.List;

@Slf4j
@Getter
public class MarchTroopBean implements JsonObject {
    @JsonProperty("id")
    private long id = GameEngine.getInstance().nextId();
    @JsonProperty("mirror")
    private boolean mirror = false;
    @JsonProperty("marchHeroBeans")
    private List<MarchHeroBean> marchHeroBeans;
    @JsonProperty("marchSoldierBeans")
    private List<MarchSoldierBean> marchSoldierBeans;

    public MarchTroopBean(List<MarchHeroBean> marchHeroBeans, List<MarchSoldierBean> marchSoldierBeans) {
        this.marchHeroBeans = marchHeroBeans;
        this.marchSoldierBeans = marchSoldierBeans;
    }

    public boolean isDead() {
        return getSoldierAmount() <= 0;
    }

    public long getSoldierAmount() {
        long amount = 0;
        for (MarchSoldierBean marchSoldierBean : marchSoldierBeans) {
            amount += marchSoldierBean.getSoldierAmount();
        }
        return amount;
    }

    public void returnTroopData(Player player) {
        if (mirror) {
            return;
        }
        if (player == null) {
            throw new ServiceException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        player.submit(() -> {
            for (MarchHeroBean marchHeroBean : marchHeroBeans) {
//                PlayerHero playerHero = player.heroAgent.getPlayerHero(marchHeroBean.getHeroId());
            }
        });
    }

    public int getSpeed() {
        int speed = Integer.MAX_VALUE;
        for (MarchSoldierBean marchSoldierBean : marchSoldierBeans) {
            int soldierSpeed = marchSoldierBean.getSpeed();
            if (speed > soldierSpeed) {
                speed = soldierSpeed;
            }
        }
        return (speed == Integer.MAX_VALUE || speed <= 0) ? MarchUtil.DEFAULT_MARCH_SPEED : speed;
    }

    public long getLoad() {
        long load = 0;
        for (MarchSoldierBean marchSoldierBean : marchSoldierBeans) {
            load += marchSoldierBean.getLoad();
        }
        return load;
    }

    public long getPower() {
        long power = 0;
        for (MarchHeroBean marchHeroBean : marchHeroBeans) {
            power += marchHeroBean.getPower();
        }
        for (MarchSoldierBean marchSoldierBean : marchSoldierBeans) {
            power += marchSoldierBean.getPower();
        }
        return power;
    }
}
