package org.pot.game.engine.enums;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.enums.LabelEnum;
import org.pot.common.util.EnumUtils;

import java.util.Map;

@Slf4j
public enum ItemSubType implements LabelEnum {
    FOOD("food"),
    WOOD("wood"),
    STEEL("steel"),
    GAS("gas"),
    VIP_POINT("vipPoint"),
    GENERAL("general"),
    MOVE_BACK("moveBack"),
    RAPID("rapid"),
    RAPID_ALL("rapidAll"),
    BUILD("build"),
    TRAIN("train"),
    RESEARCH("research"),
    TREAT("cure"),
    FRAGMENT("fragment"),
    BOX_RESOURCE("boxResource"),
    BOX_DIAMOND("boxDiamond"),
    BOX_ONE("boxOne"),
    BOX_DROP("boxDrop"),
    BOX_ALL("boxAll"),
    SUPPLY_COIN("supplyCoin"),
    RANDOM_MOVE("randomMove"),
    SHIELD("shield"),
    HAMMER("hammer"),
    GOD_EXP("godExp"),
    godResA("godResA"),
    godResB("godResB"),
    PLAYER_EXP("playerExp");

    static {
        map = EnumUtils.toLabelMap(values());
    }

    private static final Map<String, ItemSubType> map;

    final String value;

    public static ItemSubType getItemSubType(String itemSubTypeString) {
        ItemSubType itemSubType = map.get(itemSubTypeString);
        if (itemSubType == null)
            log.error("ItemSubType findById is null, id:{}", itemSubTypeString);
        return itemSubType;
    }

    public String getLabel() {
        return this.value;
    }

    ItemSubType(String value) {
        this.value = value;
    }

    public boolean isMatchItem(String itemSubTypeString) {
        return equals(getItemSubType(itemSubTypeString));
    }
}

