package org.pot.game.engine.world.module.map.clean;

import lombok.Data;

@Data
public class WorldMapBlockCleanInfo implements Comparable<WorldMapBlockCleanInfo> {
    private int blockId;
    private long cleanTimestamp;

    public WorldMapBlockCleanInfo(int blockId, long cleanTimestamp) {
        this.blockId = blockId;
        this.cleanTimestamp = cleanTimestamp;
    }

    @Override
    public int compareTo(WorldMapBlockCleanInfo o) {
        return Long.compare(cleanTimestamp, o.cleanTimestamp);
    }
}
