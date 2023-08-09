package org.pot.game.engine.switchcontrol;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.date.DateTimeString;
import org.pot.game.engine.GameEngine;
import org.pot.game.resource.switchcontrol.SwitchControl;

public class SwitchTimeUtil {
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
    }

    private static long getEndTime(SwitchControl swc, Long startTime) {
        return 0;
    }

    private static long getRequireOpenTime(SwitchControl swc, SwitchRecord swr) {
        return 0;
    }

    private static long getFirstOpenTime(SwitchControl swc, SwitchRecord swr) {
        return 0;
    }

}
