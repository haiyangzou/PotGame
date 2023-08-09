package org.pot.game.engine.switchcontrol;

import org.pot.game.engine.WorldManager;
import org.pot.game.engine.switchcontrol.listener.WorldSwitchListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SwitchRegistry {
    private static final CopyOnWriteArrayList<ISwitchListener> all = new CopyOnWriteArrayList<>();
    private static final Map<String, CopyOnWriteArrayList<ISwitchListener>> map = new ConcurrentHashMap<>();

    static {
//        register(GiftSwitchListener.INSTANCE)
    }

    public static ISwitchListener wrap(ISwitchListener switchListener) {
        if (WorldManager.isWorldThread()) {
            return new WorldSwitchListener(switchListener);
        }
        return switchListener;
    }

    public static void register(ISwitchListener switchListener) {
        all.addIfAbsent(wrap(switchListener));
    }

    public static List<ISwitchListener> getAllSwitchListenerList() {
        return all;
    }

    public static List<ISwitchListener> getSwitchListenerList(String switchId) {
        CopyOnWriteArrayList<ISwitchListener> list = map.get(switchId);
        return list == null ? Collections.emptyList() : list;
    }
}
