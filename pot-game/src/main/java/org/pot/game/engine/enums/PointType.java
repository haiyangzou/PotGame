package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.point.PointExtraData;

import java.util.Map;

@Getter
public enum PointType implements IntEnum {
    CITY(1, 2, 2, false, PointCityData.class),
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
}
