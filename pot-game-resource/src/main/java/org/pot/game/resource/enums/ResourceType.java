package org.pot.game.resource.enums;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;
import org.slf4j.Logger;

import java.util.Map;

@Slf4j
public enum ResourceType implements IntEnum {
    FOOD(1),
    WOOD(2),
    STONE(3),
    MINE(4),
    HERO_EXP(5),
    POWER(6),
    ENERGY(7),
    EXP(8),
    UNION_CURRENCY(9),
    RARE_EARTH_CURRENCY(10),
    DIAMOND(11),
    DIAMOND_INNER(12),
    DIAMOND_MIX(13),
    MARS_EXP(14),
    MARS_RESOURCE_A(15),
    MARS_RESOURCE_B(16),
    HERO_SCORE(17),
    VIP_POINT(18),
    HERO(90),
    SOLDIER(98),
    ITEM(99);

    static {
        typeMap = EnumUtils.toMap(values());
    }

    private static final Map<Integer, ResourceType> typeMap;

    @Override
    public int getId() {
        return this.type;
    }

    public static ResourceType getResourceType(int type) {
        ResourceType resourceType = typeMap.get(Integer.valueOf(type));
        if (resourceType == null)
            log.error("resourceType findById is null, id:{}", Integer.valueOf(type));
        return resourceType;
    }

    final int type;

    ResourceType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
