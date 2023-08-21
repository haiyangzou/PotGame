package org.pot.game.engine.march.war;

import org.pot.common.util.CollectionUtil;
import org.pot.game.engine.march.MarchManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WarManager {
    private final MarchManager marchManager;
    private final Map<Integer, CopyOnWriteArrayList<String>> lairWarMap = new ConcurrentHashMap<>();
    private final Map<Integer, CopyOnWriteArrayList<String>> unionWarMap = new ConcurrentHashMap<>();

    public WarManager(MarchManager marchManager) {
        this.marchManager = marchManager;
    }

    public boolean existLairWar(Integer pointId) {
        return CollectionUtil.isNotEmpty(lairWarMap.get(pointId));
    }
}
