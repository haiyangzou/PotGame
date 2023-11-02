package org.pot.game.engine.scene;

import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;

import java.util.Map;

public enum ViewLevel implements IntEnum {
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    ;

    @Override
    public int getId() {
        return 0;
    }

    public static ViewLevel findById(int id) {
        return map.get(id);
    }

    public static final Map<Integer, ViewLevel> map = EnumUtils.toMap(ViewLevel.values());
    @Getter
    private final int id;

    ViewLevel(int id) {
        this.id = id;
    }
}
