package org.pot.game.engine.switchcontrol;

import lombok.Getter;
import org.pot.game.resource.switchcontrol.SwitchControl;
import org.pot.game.resource.switchcontrol.SwitchControlConfig;

import java.util.concurrent.TimeUnit;

@Getter
public class SwitchRecord {
    private volatile SwitchState state = SwitchState.CLOSED;
    private volatile String id;
    private volatile long startTime = -1;
    private volatile long endTime;

    public SwitchRecord(String id) {
        this.id = id;
    }

    void setState(SwitchState state) {
        this.state = state;
    }

    void setNeverOpen() {
        this.state = SwitchState.CLOSED;
        this.startTime = this.endTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365);
    }

    void setTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getPreviewStartTime() {
        SwitchControl switchControl = SwitchControlConfig.getInstance().getSpec(id);
        if (switchControl == null) {
            return -1;
        }
        if (switchControl.getNoticeTimeBefore() <= 0) {
            return -1;
        }
        return startTime - TimeUnit.SECONDS.toMillis(switchControl.getNoticeTimeBefore());
    }

    SwitchSignal getNextSignal() {
        SwitchControl switchControl = SwitchControlConfig.getInstance().getSpec(id);
        if (switchControl == null) {
            setNeverOpen();
            return null;
        }
        if (state == SwitchState.CLOSED) {
            SwitchTimeUtil.correct(switchControl, this);
        }
        final long currentTime = System.currentTimeMillis();
        final long previewStartTime = getPreviewStartTime();
        if (previewStartTime > 0) {
            if (currentTime < previewStartTime) {
                return new SwitchSignal(id, previewStartTime, SwitchState.PREVIEWING, (r, l) -> l.onPreviewStart(r));
            }
            if (currentTime < startTime && state != SwitchState.PREVIEWING) {
                return new SwitchSignal(id, previewStartTime, SwitchState.PREVIEWING, (r, l) -> l.onPreviewStart(r));
            }
        }
        if (currentTime < startTime) {
            return new SwitchSignal(id, startTime, SwitchState.OPENING, (r, l) -> l.onOpen(r));
        }
        if (currentTime < endTime && state != SwitchState.OPENING) {
            return new SwitchSignal(id, startTime, SwitchState.OPENING, (r, l) -> l.onOpen(r));
        }
        final long showEndTime = getShowEndTime();
        if (currentTime < endTime) {
            return new SwitchSignal(id, endTime, showEndTime > 0 ? SwitchState.SHOWING : SwitchState.CLOSED, (r, l) -> l.onClose(r));
        }
        if (showEndTime > 0) {
            if (currentTime < showEndTime) {
                if (state != SwitchState.SHOWING) {
                    return new SwitchSignal(id, endTime, SwitchState.SHOWING, (r, l) -> l.onClose(r));
                } else {
                    return new SwitchSignal(id, showEndTime, SwitchState.CLOSED, (r, l) -> l.onShowEnd(r));
                }
            }
            if (state == SwitchState.SHOWING) {
                return new SwitchSignal(id, showEndTime, SwitchState.CLOSED, (r, l) -> l.onShowEnd(r));
            }
        } else {
            if (state != SwitchState.CLOSED) {
                return new SwitchSignal(id, endTime, SwitchState.CLOSED, (r, l) -> l.onClose(r));
            }
        }
        return null;
    }

    public long getShowEndTime() {
        SwitchControl switchControl = SwitchControlConfig.getInstance().getSpec(id);
        if (switchControl == null) {
            return -1;
        }
        if (switchControl.getShowTimeAfter() <= 0) {
            return -1;
        }
        return endTime + TimeUnit.SECONDS.toMillis(switchControl.getShowTimeAfter());
    }

    public SwitchControl getSwitchControl() {
        return SwitchControlConfig.getInstance().getSpec(id);
    }
}
