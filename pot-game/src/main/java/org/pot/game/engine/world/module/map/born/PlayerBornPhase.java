package org.pot.game.engine.world.module.map.born;

import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;

import java.util.Map;

@Getter
public enum PlayerBornPhase implements IntEnum {
    PHASE_1(1, 15, 2, 4),
    PHASE_2(2, 30, 1, 4),
    PHASE_3(3, 45, 1, 6);
    private final int id;
    private final int bornCountLimit;
    private final int bornMinBandId;
    private final int bornMaxBandId;
    private static final Map<Integer, PlayerBornPhase> map = EnumUtils.toMap(PlayerBornPhase.values());

    PlayerBornPhase(int id, int bornCountLimit, int bornMinBandId, int bornMaxBandId) {
        this.id = id;
        this.bornCountLimit = bornCountLimit;
        this.bornMinBandId = bornMinBandId;
        this.bornMaxBandId = bornMaxBandId;
    }

    public static PlayerBornPhase find(int id) {
        return map.getOrDefault(id, PHASE_1);
    }
}
