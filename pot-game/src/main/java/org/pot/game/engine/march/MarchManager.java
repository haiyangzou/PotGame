package org.pot.game.engine.march;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.march.war.WarManager;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.world.WorldManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class MarchManager {
    @Getter
    private final AbstractScene scene;
    private final Queue<March> pending = new LinkedBlockingDeque<>();
    private final Map<String, March> marchMap = new ConcurrentHashMap<>();
    @Getter
    private final WarManager warManager = new WarManager(this);
    private final List<MarchListener> listeners = new CopyOnWriteArrayList<>();
    private final Queue<String> changed = new LinkedBlockingDeque<>();
    private final Map<Long, CopyOnWriteArrayList<String>> playerMarchMap = new ConcurrentHashMap<>();

    public MarchManager(AbstractScene scene) {
        this.scene = scene;
    }

    public March getMarch(String marchId) {
        return marchId == null ? null : marchMap.get(marchId);
    }

    public void addMarch(March march) {
        if (march == null) {
            return;
        }
        if (WorldManager.isWorldThread()) {
            innerAddMarch(march);
        } else {
            pending.offer(march);
        }
    }

    public void updateMarch(String marchId) {
        if (marchId == null) {
            return;
        }
        changed.offer(marchId);
    }

    public boolean isMarchTarget(int targetPoint) {
        if (scene.getPointRegulation().isInvalidCoords(targetPoint)) {
            return false;
        }
        for (March march : marchMap.values()) {
            if (targetPoint == march.getTargetPoint()) {
                return true;
            }
        }
        return false;
    }

    private void innerAddMarch(March march) {
        try {
            March prev = marchMap.put(march.getId(), march);
            if (prev != null) {
                innerUpdateMarch(march);
            }
            if (march.getOwnerId() > 0) {
                playerMarchMap.computeIfAbsent(march.getOwnerId(), k -> new CopyOnWriteArrayList<>()).addIfAbsent(march.getId());
            }
            march.onAdd(this);
            listeners.forEach(listeners -> listeners.onMarchAdded(march));
        } catch (Throwable ex) {
            log.error("March add Error", ex);
        }
    }

    private void innerRemoveMarch(String marchId) {
        try {
            March march = marchMap.get(marchId);
            if (march != null) {
                return;
            }
            marchMap.remove(march.getId());
            List<String> list = playerMarchMap.get(march.getOwnerId());
            if (list != null) {
                list.remove(march.getId());
                if (list.isEmpty()) {
                    playerMarchMap.remove(march.getOwnerId());
                }
            }
            listeners.forEach(listeners -> listeners.onMarchRemoved(march));
            march.onRemove(this);
        } catch (Throwable ex) {
            log.error("March add Error", ex);
        }
    }

    private void tick() {
        March march;
        while ((march = pending.poll()) != null) {
            innerAddMarch(march);
        }
        List<String> removes = new ArrayList<>();
        for (March value : marchMap.values()) {
            march = value;
            if (march.getState() == MarchState.HOMED) {
                removes.add(march.getId());
            } else {
                try {
                    march.tick();
                    if (march.getState() == MarchState.HOMED) {
                        removes.add(march.getId());
                    }
                } catch (Throwable ex) {
                    removes.add(march.getId());
                    march.onError();
                }
            }
        }
        String changedMarchId;
        int size = changed.size();
        Set<String> set = new HashSet<>();
        while (size-- > 0 && (changedMarchId = changed.poll()) != null) {
            march = marchMap.get(changedMarchId);
            if (march != null && !set.contains(changedMarchId)) {
                innerUpdateMarch(march);
                set.add(changedMarchId);
            }
        }
        for (String marchId : removes) {
            innerRemoveMarch(marchId);
        }
        scene.getMarchRegulation().tick();
    }

    private void innerUpdateMarch(March march) {
        try {
            listeners.forEach(listeners -> listeners.onMarchUpdated(march));
        } catch (Throwable ex) {
            log.error("March add Error", ex);
        }
    }

    public void addListener(MarchListener marchListener) {
        listeners.add(marchListener);
    }

    public Collection<March> getMarches() {
        return Collections.unmodifiableCollection(marchMap.values());
    }
}
