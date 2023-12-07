package org.pot.game.engine.player.component;

import org.pot.game.engine.enums.IStatisticsEnum;
import org.pot.game.engine.enums.SpeedUpType;
import org.pot.game.engine.enums.StatisticsServerEnum;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.module.event.event.SpeedUpTime;

public class PlayerStatisticsComponent {
    private final Player player;

    public PlayerStatisticsComponent(Player player) {
        this.player = player;
    }

    public void pushStatistics(IStatisticsEnum statisticsEnum) {
    }

    public int getStatisticsInt(IStatisticsEnum statisticsEnum) {
        return this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().getInt(statisticsEnum);
    }

    public long getStatisticsLong(IStatisticsEnum statisticsEnum) {
        return this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().getLong(statisticsEnum);
    }

    public float getStatisticsFloat(IStatisticsEnum statisticsEnum) {
        return this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().getFloat(statisticsEnum);
    }

    public String getStatisticsString(IStatisticsEnum statisticsEnum) {
        return this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().getString(statisticsEnum);
    }

    public boolean getStatisticsBoolean(IStatisticsEnum statisticsEnum) {
        return this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().getBoolean(statisticsEnum);
    }

    public void putStatisticsInt(IStatisticsEnum statisticsEnum, int value) {
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putInt(statisticsEnum, value);
        pushStatistics(statisticsEnum);
    }

    public void addOneStatisticsInt(IStatisticsEnum statisticsEnum) {
        int statisticsInt = getStatisticsInt(statisticsEnum);
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putInt(statisticsEnum, statisticsInt + 1);
        pushStatistics(statisticsEnum);
    }

    public void addStatisticsInt(IStatisticsEnum statisticsEnum, int value) {
        int statisticsInt = getStatisticsInt(statisticsEnum);
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putInt(statisticsEnum, statisticsInt + value);
        pushStatistics(statisticsEnum);
    }

    public void putStatisticsLong(IStatisticsEnum statisticsEnum, long value) {
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putLong(statisticsEnum, value);
        pushStatistics(statisticsEnum);
    }

    public void addStatisticsLong(IStatisticsEnum statisticsEnum, long value) {
        long statisticsLong = getStatisticsLong(statisticsEnum);
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putLong(statisticsEnum, statisticsLong + value);
        pushStatistics(statisticsEnum);
    }

    public void putStatisticsLongMax(IStatisticsEnum statisticsEnum, long value) {
        long oldValue = getStatisticsLong(statisticsEnum);
        if (value > oldValue)
            this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putLong(statisticsEnum, value);
        pushStatistics(statisticsEnum);
    }

    public void putStatisticsFloat(IStatisticsEnum statisticsEnum, float value) {
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putFloat(statisticsEnum, value);
        pushStatistics(statisticsEnum);
    }

    public void putStatisticsString(IStatisticsEnum statisticsEnum, String value) {
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putString(statisticsEnum, value);
        pushStatistics(statisticsEnum);
    }

    public void putStatisticsBoolean(IStatisticsEnum statisticsEnum, boolean value) {
        this.player.commonAgent.getPlayerCommonInfo().getPlayerStatistics().putBoolean(statisticsEnum, value);
        pushStatistics(statisticsEnum);
    }

    public void speedUpTime(long value, SpeedUpType speedUpType) {
        long statisticsLong = getStatisticsLong(StatisticsServerEnum.SPEED_UP_TOTAL_TIME);
        putStatisticsLong(StatisticsServerEnum.SPEED_UP_TOTAL_TIME, statisticsLong + value);
        this.player.eventComponent.postPlayerEvent(SpeedUpTime.builder().type(speedUpType.getType()).speedUpTime(value).build());
    }
}
