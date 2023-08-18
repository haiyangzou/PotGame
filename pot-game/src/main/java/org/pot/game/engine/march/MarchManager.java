package org.pot.game.engine.march;

import lombok.Getter;
import org.pot.game.engine.scene.AbstractScene;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class MarchManager {
    @Getter
    private final AbstractScene scene;
    private final Queue<March> pending = new LinkedBlockingDeque<>();
    private final Map<String, March> marchMap = new ConcurrentHashMap<>();

    public MarchManager(AbstractScene scene) {
        this.scene = scene;
    }

    public March getMarch(String marchId) {
        return marchId == null ? null : marchMap.get(marchId);
    }
}
