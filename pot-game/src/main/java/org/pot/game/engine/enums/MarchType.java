package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;

public enum MarchType implements IntEnum {
    SCOUT(1, false),
    ATTACK(2, false),
    GATHER(3, false),
    ENCAMPMENT(4, false),
    REINFORCEMENT(5, false),
    RALLY_ATTACK(6, false),
    RALLY_ASSEMBLY(7, false),
    MONSTER_ATTACK(8, false),
    UNION_TRADE(9, false),
    UNION_DEPOT_STORE(10, false),
    UNION_DEPOT_FETCH(11, false),
    RADAR(12, false),
    GARRISON(13, false),
    VISIT(14, false),
    UNION_DEFEND(15, false),
    UNION_TREE(16, false),
    ;
    @Getter
    int id;
    @Getter
    boolean overLimit;

    MarchType(int id, boolean overLimit) {
        this.id = id;
        this.overLimit = overLimit;
    }
}
