package org.pot.game.engine.world.module.map.monster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorldMonsterBlockRefreshInfo {
    private int blockId;
    private long refreshTimestamp;
}
