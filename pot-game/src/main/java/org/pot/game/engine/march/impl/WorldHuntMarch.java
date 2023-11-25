package org.pot.game.engine.march.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.march.BaseMarch;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.march.dead.DeadSoldier;
import org.pot.game.resource.common.Reward;
import org.pot.message.protocol.world.WorldMarchInfo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@JsonTypeName("WorldHuntMarch")
public class WorldHuntMarch extends BaseMarch {
    @JsonProperty("expendPower")
    private long expendPower;
    @JsonProperty("monsterId")
    private int monsterId;
    @JsonProperty("battleId")
    private long battleId;
    @JsonProperty("win")
    private boolean win = false;
    @JsonProperty("rewards")
    private List<Reward> reward = new CopyOnWriteArrayList<>();
    @JsonProperty("wounded")
    private List<DeadSoldier> wounded = Collections.emptyList();

    public WorldHuntMarch(MarchManager marchManager, long ownerId, int sourcePoint, int targetPoint, MarchTroopBean marchTroopBean, long expendPower) {
        super(marchManager, MarchType.ATTACK, sourcePoint, targetPoint);
        this.expendPower = expendPower;
    }

    @Override
    public WorldMarchInfo.Builder buildWorldMarchInfo(long viewerId) {
        return null;
    }

    @Override
    public void tick() {
        if (state == MarchState.MARCHING) {
            marching();
        } else if (state == MarchState.RETURNING) {
            returning();
        } else if (state == MarchState.ARRIVED) {
            arrived();
        }
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    private void arrived() {

    }

    private void returning() {

    }

    private void marching() {
        if (System.currentTimeMillis() >= this.endTime) {
            this.state = MarchState.ARRIVED;
            this.fireUpdate();
        }
    }


}
