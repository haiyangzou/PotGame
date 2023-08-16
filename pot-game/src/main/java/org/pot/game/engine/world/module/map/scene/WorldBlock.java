package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.pot.common.util.PointUtil;

import java.util.ArrayList;
import java.util.List;

public class WorldBlock {
    @Getter
    private final int id;
    @Getter
    private final int x, y;
    @Getter
    private final List<Integer> pointIds;
    @Getter
    private final List<Integer> inBlackEarthPointIds;
    @Getter
    private final List<Integer> notInBlackEarthPointIds;

    public WorldBlock(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        List<Integer> temp = new ArrayList<>();
        for (int xCoordinate = x; xCoordinate < x + WorldMapPointRegulation.BLOCK_LENGTH_X; xCoordinate++) {
            for (int yCoordinate = y; yCoordinate < y + WorldMapPointRegulation.BLOCK_LENGTH_Y; yCoordinate++) {
                if (WorldMapScene.instance.getPointRegulation().isValidCoordinate(xCoordinate, yCoordinate)) {
                    temp.add(PointUtil.getPointId(xCoordinate, yCoordinate));
                }
            }
        }
        pointIds = ImmutableList.copyOf(temp);
        inBlackEarthPointIds = ImmutableList.copyOf(temp);
        notInBlackEarthPointIds = ImmutableList.copyOf(temp);
    }
}
