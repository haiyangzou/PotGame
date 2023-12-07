package org.pot.game.engine.enums;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum StatisticsEnum implements IStatisticsEnum {
    MONSTER_MAX_LEVEL, MONSTER_RALLY_MAX_LEVEL, KILL_SOLDIER, DAILY_LOGIN_REWARD, TOTAL_GAIN_AMOUNT, DAILY_WORSHIP_POWER, DAILY_WORSHIP_KILL, LOGIN_COUNT, EQUIP_POWER, EQUIP_POWER_MAX, CHIP_POWER, CHIP_POWER_MAX, ELEMENT_POWER, ELEMENT_POWER_MAX, CUBE_POWER, CUBE_POWER_MAX, NANO_WEAPON_POWER, NANO_WEAPON_POWER_MAX, HERO_POWER, SOLIDER_POWER, TECH_POWER, BUILDING_POWER, PLAYER_LEVEL_POWER, GLORY_LEVEL_POWER, DRAGON_POWER, WIN_RECORD, LOSE_RECORD, ATK_WIN_RECORD, ATK_LOSE_RECORD, DEF_WIN_RECORD, DEF_LOSE_RECORD, DESTROY_SOLIDER_RECORD, DEAD_SOLIDER_RECORD, CURE_SOLIDER_RECORD, INJURED_SOLIDER_RECORD, DESTROY_HQ_RECORD, DEAD_HQ_RECORD, DESTROY_POWER_RECORD;

    private static final Set<String> names;

    static {
        names = ImmutableSet.copyOf(Arrays.stream(values()).map(Enum::name).collect(Collectors.toSet()));
    }

    public static Map<String, String> needSendClient(Map<String, String> varMap) {
        return varMap.entrySet().stream().filter(entry -> names.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getStatisticsName() {
        return name();
    }
}
