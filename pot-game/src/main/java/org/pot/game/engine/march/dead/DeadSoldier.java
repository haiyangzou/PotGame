package org.pot.game.engine.march.dead;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

public class DeadSoldier implements Comparable<DeadSoldier>, JsonObject, Serializable {
    @JsonProperty("id")
    private int id;
    @JsonProperty("initAmount")
    private long initAmount;
    @JsonProperty("liveAmount")
    private long liveAmount;
    @JsonProperty("lossAmount")
    private long lossAmount;
    @JsonProperty("hurtAmount")
    private long hurtAmount;
    @JsonProperty("deadAmount")
    private long deadAmount;
    @JsonProperty("reliveHurtAmount")
    private long reliveHurtAmount;
    @JsonProperty("reliveDeadAmount")
    private long reliveDeadAmount;

    @Override
    public int compareTo(DeadSoldier o) {
        return 0;
    }
}
