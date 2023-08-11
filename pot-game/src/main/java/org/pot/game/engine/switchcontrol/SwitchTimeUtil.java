package org.pot.game.engine.switchcontrol;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.date.DateTimeString;
import org.pot.common.util.ExDateTimeUtil;
import org.pot.game.engine.GameEngine;
import org.pot.game.resource.switchcontrol.SwitchControl;

import java.util.concurrent.TimeUnit;

public class SwitchTimeUtil {
    public static Range<Long> getNextCircleTime(SwitchRecord swr) {
        SwitchControl swc = swr.getSwitchControl();
        if (swc == null) {
            return Range.between(0L, 0L);
        } else if (SwitchType.isOnce(swc.getOpenType())) {
            return Range.between(swr.getStartTime(), swr.getEndTime());
        } else {
            final long now = System.currentTimeMillis();
            if (now < swr.getStartTime()) {
                return Range.between(swr.getStartTime(), swr.getEndTime());
            } else {
                return getNextCircleTime(swc, swr, swr.getEndTime());
            }
        }
    }

    private static Range<Long> getNextCircleTime(SwitchControl swc, SwitchRecord swr, long based) {
        //首次开启时间
        final long firstOpenTime = getFirstOpenTime(swc, swr);
        //要求开启时间，要求在服务器开始时间之后的摸个时间才能开启
        final long requireOpenTime = getRequireOpenTime(swc, swr);
        //要求结束时间,
        final long requireEndTime = Math.max(based, firstOpenTime);
        //
        final long serverOpenTime = ExDateTimeUtil.getDayStart(GameEngine.getInstance().getOpenTime());
        final long finalEndTime = StringUtils.isBlank(swc.getFinalEnds()) ? -1 : DateTimeString.DATE_TIME.toTimestamp(swc.getFinalEnds());
        final long finalEndTimeAfterServerOpen = swc.getFinalEndsOpenDate() <= 0 ? -1L : SwitchTimeUtil.adjustTime(serverOpenTime, swc.getFinalEndsOpenDate());
        long endTime, startTime = firstOpenTime;
        while (true) {
            endTime = getEndTime(swc, startTime);
            //开启时间>=要求开启时间 && 结束时间>要求结束时间
            if (startTime >= requireOpenTime && endTime > requireEndTime) {
                break;
            }
            if (swc.getOpens() <= 0) {
                break;
            }
            long nextStartTime = getNextCircleTime(swc, endTime);

        }
        return null;
    }

    private static long adjustTime(long mills, long days, long hours, long minutes, long seconds) {
        long rtn = mills;
        if (days > 0) {
            rtn += TimeUnit.DAYS.toMillis(days);
        }
        if (hours > 0) {
            rtn += TimeUnit.HOURS.toMillis(hours);
        }
        if (minutes > 0) {
            rtn += TimeUnit.MINUTES.toMillis(minutes);
        }
        if (seconds > 0) {
            rtn += TimeUnit.SECONDS.toMillis(seconds);
        }
        return rtn;
    }

    private static long adjustTime(long millis, long days) {
        return adjustTime(millis, days, 0, 0, 0);
    }

    private static long getNextCircleTime(SwitchControl swc, long endTime) {
        long startTime;
        if (swc.isCloseDays()) {
            startTime = adjustTime(endTime, swc.getCloses());
        } else {
            startTime = endTime + TimeUnit.SECONDS.toMillis(swc.getCloses());
        }
        return startTime;
    }

    static void correct(SwitchControl swc, SwitchRecord swr) {
        if (StringUtils.isNotBlank(swc.getCloseByServerTime())) {
            long closeByServerTime = DateTimeString.DATE_TIME.toTimestamp(swc.getCloseByServerTime());
            if (GameEngine.getInstance().getOpenTime() >= closeByServerTime) {
                swr.setNeverOpen();
                return;
            }
        }
        if (StringUtils.isNotBlank(swc.getCloseBeforeServerTime())) {
            long closeBeforeServerTime = DateTimeString.DATE_TIME.toTimestamp(swc.getCloseBeforeServerTime());
            if (GameEngine.getInstance().getOpenTime() <= closeBeforeServerTime) {
                swr.setNeverOpen();
                return;
            }
        }
        if (SwitchType.isOnce(swc.getOpenType())) {
            final long firstOpenTime = getFirstOpenTime(swc, swr);
            final long requireOpenTime = getRequireOpenTime(swc, swr);
            final long onceStartTime = Math.max(firstOpenTime, requireOpenTime);
            final long onceEndTime = getEndTime(swc, onceStartTime);
            swr.setTime(onceStartTime, onceEndTime);
            return;
        }
        //循环开关进行检验，属于合理时间范围内，不重新计算
        //1.开启时间>0
        //2.结束时间>0
        //3.开始时间<结束时间
        //4.结束时间 大于 当前时间 或者 展示结束时间 大于当前时间
        final long currentTime = System.currentTimeMillis();
        if (swr.getStartTime() > 0
                && swr.getEndTime() > 0
                && swr.getStartTime() < swr.getEndTime()
                && (swr.getEndTime() > currentTime || swr.getShowEndTime() > currentTime)) {
            return;
        }
        Range<Long> nextCircleTime = SwitchTimeUtil.getNextCircleTime(swc, swr, currentTime);
        swr.setTime(nextCircleTime.getMinimum(), nextCircleTime.getMaximum());
    }

    private static long getEndTime(SwitchControl swc, Long startTime) {
        long endTime;
        if (swc.isOpenDays()) {
            endTime = adjustTime(startTime, swc.getOpens());
        } else {
            endTime = startTime + TimeUnit.SECONDS.toMillis(swc.getOpens());
        }
        return endTime;
    }

    private static long getRequireOpenTime(SwitchControl swc, SwitchRecord swr) {
        if (isFirstCircle(swc, swr) && StringUtils.isNotBlank(swc.getCircleFirstStartTime())) {
            return -1;
        }
        final long serverOpenTime = ExDateTimeUtil.getDayStart(GameEngine.getInstance().getOpenTime());
        return swc.getServerOpenDays() <= 0 ? -1L : adjustTime(serverOpenTime, swc.getServerOpenDays());
    }

    private static boolean isFirstCircle(SwitchControl swc, SwitchRecord swr) {
        return SwitchType.isCircle(swc.getOpenType()) && swr.getStartTime() < 0;
    }

    private static long getFirstOpenTime(SwitchControl swc, SwitchRecord swr) {
        long begin;
        String firstOpenTime;
        if (isFirstCircle(swc, swr)) {
            firstOpenTime = StringUtils.defaultIfBlank(swc.getCircleFirstStartTime(), swc.getStartTime());
        } else {
            firstOpenTime = swc.getStartTime();
        }
        if (StringUtils.equalsAnyIgnoreCase("serverOpenTime", firstOpenTime)) {
            begin = ExDateTimeUtil.getDayStart(GameEngine.getInstance().getOpenTime());
        } else {
            begin = DateTimeString.DATE_TIME.toTimestamp(firstOpenTime);
        }
        return adjustTime(begin, swc.getStartAddDays(), swc.getStartAddHour(), swc.getStartAddMinute(), swc.getStartAddSecond());
    }

}
