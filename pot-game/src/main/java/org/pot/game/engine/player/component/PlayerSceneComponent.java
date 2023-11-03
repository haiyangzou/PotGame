package org.pot.game.engine.player.component;

import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.world.module.instance.InstanceModule;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;

public class PlayerSceneComponent {
    private final Player player;

    public PlayerSceneComponent(Player player) {
        this.player = player;
    }

    public AbstractScene getScene() {
        if (GameServerInfo.isGameServer()) {
            return WorldMapScene.singleton;
        } else if (GameServerInfo.isSlaveServer()) {
            return InstanceModule.singleton().getInstanceForParticipant(player.getUid()).getScene();
        } else {
            return WorldMapScene.singleton;
        }
    }
}
