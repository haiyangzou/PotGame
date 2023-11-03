package org.pot.game.engine.world.module.instance;

import org.pot.game.engine.world.module.instance.scene.InstanceScene;

public abstract class Instance {
    public abstract boolean isParticipant(long playerId);

    public abstract InstanceScene getScene();
}
