package org.pot.game.engine.player.module.common;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.NumberUtil;
import org.pot.game.engine.enums.IStatisticsEnum;
import org.pot.game.engine.enums.StatisticsItemEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatistics {
    private Map<String, String> varMap = new ConcurrentHashMap<>();

    public Map<String, String> getVarMap() {
        return this.varMap;
    }

    public void setVarMap(Map<String, String> varMap) {
        this.varMap = varMap;
    }

    public boolean containsKey(IStatisticsEnum statisticsEnum) {
        return this.varMap.containsKey(statisticsEnum.getStatisticsName());
    }

    public void putBoolean(IStatisticsEnum statisticsEnum, boolean value) {
        this.varMap.put(statisticsEnum.getStatisticsName(), Boolean.toString(value));
    }

    public boolean getBoolean(IStatisticsEnum statisticsEnum) {
        return BooleanUtils.toBoolean(this.varMap.get(statisticsEnum.getStatisticsName()));
    }

    public boolean getBoolean(IStatisticsEnum statisticsEnum, boolean defaultValue) {
        return containsKey(statisticsEnum) ? getBoolean(statisticsEnum) : defaultValue;
    }

    public void putDouble(IStatisticsEnum statisticsEnum, double value) {
        this.varMap.put(statisticsEnum.getStatisticsName(), NumberUtil.plainString(value));
    }

    public double getDouble(IStatisticsEnum statisticsEnum) {
        return containsKey(statisticsEnum) ? Double.parseDouble(this.varMap.get(statisticsEnum.getStatisticsName())) : 0.0D;
    }

    public double getDouble(IStatisticsEnum statisticsEnum, double defaultValue) {
        return containsKey(statisticsEnum) ? getDouble(statisticsEnum) : defaultValue;
    }

    public void putFloat(IStatisticsEnum statisticsEnum, float value) {
        this.varMap.put(statisticsEnum.getStatisticsName(), NumberUtil.plainString(value));
    }

    public float getFloat(IStatisticsEnum statisticsEnum) {
        return containsKey(statisticsEnum) ? Float.parseFloat(this.varMap.get(statisticsEnum.getStatisticsName())) : 0.0F;
    }

    public float getFloat(IStatisticsEnum statisticsEnum, float defaultValue) {
        return containsKey(statisticsEnum) ? getFloat(statisticsEnum) : defaultValue;
    }

    public void putInt(IStatisticsEnum statisticsEnum, int value) {
        this.varMap.put(statisticsEnum.getStatisticsName(), Integer.toString(value));
    }

    public int getInt(IStatisticsEnum statisticsEnum) {
        return containsKey(statisticsEnum) ? Integer.parseInt(this.varMap.get(statisticsEnum.getStatisticsName())) : 0;
    }

    public int getInt(IStatisticsEnum statisticsEnum, int defaultValue) {
        return containsKey(statisticsEnum) ? getInt(statisticsEnum) : defaultValue;
    }

    public void putLong(IStatisticsEnum statisticsEnum, long value) {
        this.varMap.put(statisticsEnum.getStatisticsName(), Long.toString(value));
    }

    public long getLong(IStatisticsEnum statisticsEnum) {
        return containsKey(statisticsEnum) ? Long.parseLong(this.varMap.get(statisticsEnum.getStatisticsName())) : 0L;
    }

    public long getLong(IStatisticsEnum statisticsEnum, long defaultValue) {
        return containsKey(statisticsEnum) ? getLong(statisticsEnum) : defaultValue;
    }

    public void putString(IStatisticsEnum statisticsEnum, String value) {
        this.varMap.put(statisticsEnum.getStatisticsName(), StringUtils.trimToEmpty(value));
    }

    public String getString(IStatisticsEnum statisticsEnum) {
        return this.varMap.get(statisticsEnum.getStatisticsName());
    }

    public String getString(IStatisticsEnum statisticsEnum, String defaultValue) {
        return containsKey(statisticsEnum) ? getString(statisticsEnum) : defaultValue;
    }

    public void onNewDay() {
        for (StatisticsItemEnum statisticsItemEnum : StatisticsItemEnum.values())
            this.varMap.remove(statisticsItemEnum.name());
    }
}
