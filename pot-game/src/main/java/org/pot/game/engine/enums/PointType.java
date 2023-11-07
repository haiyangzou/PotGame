package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;
import org.pot.game.engine.point.*;

import java.util.Map;

@Getter
public enum PointType implements IntEnum {
    NONE(-99, 1, 1, false, null),
    CITY(1, 2, 2, false, PointCityData.class),
    WONDER(-1, 1, 1, true, null),
    LAND(0, 1, 1, false, null),
    THRONE(10, 3, 3, true, PointThroneData.class),
    RESOURCE(3, 1, 1, false, PointResourceData.class),
    MONSTER(5, 1, 1, false, PointMonsterData.class),
    RALLY(6, 2, 2, false, PointRallyData.class),
    ;
    private final int id;
    private final int iRange;
    private final int jRange;
    private final boolean initial;
    private final Class<? extends PointExtraData> extraDataType;
    private static final Map<Integer, PointType> map = EnumUtils.toMap(PointType.values());

    <T extends PointExtraData> PointType(int id, int iRange, int jRange, boolean initial, Class<T> extraDataType) {
        this.id = id;
        this.iRange = iRange;
        this.jRange = jRange;
        this.initial = initial;
        this.extraDataType = extraDataType;
    }

    public static PointType find(int id) {
        return map.get(id);
    }

    public int getArea() {
        return iRange * jRange;
    }

    public boolean equals(int id) {
        return this.id == id;
    }
}
