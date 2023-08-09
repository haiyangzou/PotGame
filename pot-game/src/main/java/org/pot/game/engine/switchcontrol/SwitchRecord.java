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

    SwitchSignal getNextSignal() {
        SwitchControl switchControl = SwitchControlConfig.getInstance().getSpec(id);
        if (switchControl == null) {
            setNeverOpen();
            return null;
        }
        if (state == SwitchState.CLOSED) {
            SwitchTimeUtil.correct(switchControl, this);
        }

        return null;
    }

    public long getShowEndTime() {
        SwitchControl switchControl = SwitchControlConfig.getInstance().getSpec(id);
        return 0;
    }
}
