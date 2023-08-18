package org.pot.game.engine.world.module.map.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorldResourceBlockRefreshInfo {
    private int blockId;
    private long refreshTimestamp;
}
