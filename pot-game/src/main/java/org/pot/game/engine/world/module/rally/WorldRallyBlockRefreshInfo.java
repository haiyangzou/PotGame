package org.pot.game.engine.world.module.rally;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorldRallyBlockRefreshInfo {
    private int blockId;
    private long refreshDayId;
    private long refreshTimestamp;
}
