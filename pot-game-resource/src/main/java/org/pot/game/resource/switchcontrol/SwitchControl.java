package org.pot.game.resource.switchcontrol;

import lombok.Getter;

@Getter
public class SwitchControl {
    private String id;
    private String openType;
    private int serverOpenDays;
    private String startTime;
    /**
     * 循环的首次开启时间,用来计算首次循环时间。serverOpenDays对此不生效
     * 第一次循环之后，使用startTime+serverOpenDays进行计算后面的循环时间
     * 不配置表示直接使用startTime + serverOpenDays作为循环开启时间
     */
    private String circleFirstStartTime;
    private int startAddDays;
    private int startAddHour;
    private int startAddMinute;
    private int startAddSecond;
    /**
     * 开始持续时间是否按自然天计算
     */
    private boolean openDays;
    private long opens;
    private boolean closeDays;
    private long closes;
    private String finalEnds;
    private int finalEndsOpenDate;
    /**
     * 开服时间在这之后的服，开关将直接关闭
     */
    private String closeByServerTime;
    /**
     * 开服时间在这之前的服，开关将直接关闭
     */
    private String closeBeforeServerTime;

    private int showTimeAfter;

    private int noticeTimeBefore;
}
