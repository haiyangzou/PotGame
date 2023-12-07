package org.pot.game.engine.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public enum QueueType implements IntEnum {
    BUILDING(1, true, ItemSubType.BUILD, ItemSubType.GENERAL),
    TRAIN(2, false, ItemSubType.TRAIN, ItemSubType.GENERAL),
    RESEARCH(3, true, ItemSubType.RESEARCH, ItemSubType.GENERAL),
    TREAT(4, true, ItemSubType.TREAT, ItemSubType.GENERAL);

    static {
        INDEXES = EnumUtils.toMap(values());
    }

    final int type;

    final boolean help;

    List<ItemSubType> itemTypeList;

    private static final Map<Integer, QueueType> INDEXES;

    QueueType(int type, boolean help, ItemSubType... itemTypes) {
        this.type = type;
        this.help = help;
        if (ArrayUtils.isNotEmpty((Object[]) itemTypes)) {
            this.itemTypeList = Arrays.asList(itemTypes);
        } else {
            this.itemTypeList = Collections.emptyList();
        }
    }

    public static QueueType getQueueType(int type) {
        QueueType queueType = INDEXES.get(type);
        if (queueType == null)
            log.error("queueType findById is null, id:{}", type);
        return queueType;
    }

    @Override
    public int getId() {
        return type;
    }
}