package org.pot.game.engine.switchcontrol;

import lombok.Getter;

import java.util.Objects;
import java.util.function.BiConsumer;

@Getter
public class SwitchSignal implements Comparable<SwitchSignal> {
    private String id;
    private long timestamp;
    private SwitchState switchState;
    private BiConsumer<SwitchRecord, ISwitchListener> consumer;

    public SwitchSignal(String id, long timestamp, SwitchState switchState, BiConsumer<SwitchRecord, ISwitchListener> consumer) {
        this.id = id;
        this.timestamp = timestamp;
        this.switchState = switchState;
        this.consumer = consumer;
    }

    public void strike(SwitchRecord switchRecord, ISwitchListener switchListener) {
        consumer.accept(switchRecord, switchListener);
    }

    @Override
    public int compareTo(SwitchSignal o) {
        if (this.timestamp < o.timestamp) {
            return -1;
        } else if (this.timestamp > o.timestamp) {
            return 1;
        } else {
            return this.id.compareTo(o.id);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwitchSignal that = (SwitchSignal) o;
        return timestamp == that.timestamp && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp);
    }
}
