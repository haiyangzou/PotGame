package org.pot.game.engine.switchcontrol;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;

public class SwitchManager implements Runnable {
    @Getter
    private static final SwitchManager instance = new SwitchManager();
    private final Map<String, SwitchRecord> switchRecordMap = new ConcurrentHashMap<>();
    private final SortedSet<SwitchSignal> switchSignals = new ConcurrentSkipListSet<>();
    private ScheduledFuture<?> future;

    public SwitchManager() {
    }

    @Override
    public void run() {
        Set<String> changedSwitchIds = new LinkedHashSet<>();
        for (Iterator<SwitchSignal> itr = this.switchSignals.iterator(); itr.hasNext(); ) {
            SwitchSignal switchSignal = itr.next();
            long milliseconds = System.currentTimeMillis();
            if (switchSignal.getTimestamp() > milliseconds) {
                break;
            }
        }
    }
}
