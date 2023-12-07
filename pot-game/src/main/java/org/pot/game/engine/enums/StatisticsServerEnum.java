package org.pot.game.engine.enums;


public enum StatisticsServerEnum implements IStatisticsEnum {
    SPEED_UP_TOTAL_TIME, BUILDING_CLEAN_TOTAL, TRAIN_SOLDIER_TOTAL, DAILY_BOX_TOTAL, RADAR_EVENT_TOTAL, HERO_DRAW_TYPE_1, HERO_DRAW_TYPE_2, HERO_DRAW_TYPE_3, UNION_HELP_TOTAL;

    public String getStatisticsName() {
        return name();
    }
}
