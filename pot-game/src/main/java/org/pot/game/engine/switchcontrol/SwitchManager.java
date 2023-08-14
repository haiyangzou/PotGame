package org.pot.game.engine.switchcontrol;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.game.engine.GameEngine;
import org.pot.game.resource.switchcontrol.SwitchControl;
import org.pot.game.resource.switchcontrol.SwitchControlConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SwitchManager implements Runnable {
    @Getter
    private static final SwitchManager instance = new SwitchManager();
    private final Map<String, SwitchRecord> switchRecordMap = new ConcurrentHashMap<>();
    private final SortedSet<SwitchSignal> switchSignals = new ConcurrentSkipListSet<>();
    private ScheduledFuture<?> future;

    public SwitchManager() {
    }

    public void init() {
        initDB();
    }

    private void initDB() {
        //从数据库中加载已保存的开关数据

    }

    public void startup() {
        future = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        ThreadUtil.cancel(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, future);
        GameEngine.getInstance().getAsyncExecutor().execute(this::saveDB);
    }

    private void saveDB() {

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
            try {
                SwitchRecord switchRecord = switchRecordMap.get(switchSignal.getId());
                if (switchRecord == null) continue;
                changedSwitchIds.add(switchSignal.getId());
                switchRecord.setState(switchSignal.getSwitchState());
                for (ISwitchListener switchListener : SwitchRegistry.getAllSwitchListenerList()) {
                    switchSignal.strike(switchRecord, switchListener);
                }
                for (ISwitchListener switchListener : SwitchRegistry.getSwitchListenerList(switchSignal.getId())) {
                    switchSignal.strike(switchRecord, switchListener);
                }
                long cost = System.currentTimeMillis() - milliseconds;
                log.info("Switch Control:name={},state={},cost={}", switchSignal.getId(), switchRecord.getState(), cost);
            } catch (Exception e) {
                log.error("Exception in Switch Control:name={}", switchSignal.getId(), e);
            } finally {
                itr.remove();
            }
        }
        List<SwitchControl> switchControlList = SwitchControlConfig.getInstance().getSpecList();
        for (SwitchControl switchControl : switchControlList) {
            switchRecordMap.computeIfAbsent(switchControl.getId(), switchId -> {
                changedSwitchIds.add(switchControl.getId());
                return new SwitchRecord(switchControl.getId());
            });
        }

        if (!changedSwitchIds.isEmpty()) {
            for (String switchId : changedSwitchIds) {
                SwitchRecord switchRecord = switchRecordMap.get(switchId);
                if (switchRecord == null) continue;
                SwitchSignal switchSignal = switchRecord.getNextSignal();
                if (switchSignal != null) {
                    this.switchSignals.add(switchSignal);
                }
            }
            saveDB();
        }

    }
}
