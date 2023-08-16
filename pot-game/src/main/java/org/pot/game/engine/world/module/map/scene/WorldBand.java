package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.pot.common.util.CollectionUtil;

import java.util.Collections;
import java.util.List;

/**
 * Block带
 */
public class WorldBand {
    @Getter
    private final int id;
    @Getter
    private List<Integer> blockIds = Collections.emptyList();

    public WorldBand(int id) {
        this.id = id;
    }

    public void addBlockId(int blockId) {
        if (blockIds.contains(blockId)) {
            return;
        }
        List<Integer> temp = Lists.newArrayList(blockIds);
        temp.add(blockId);
        Collections.sort(temp);
        blockIds = ImmutableList.copyOf(temp);
    }

    public List<Integer> getDisorderlyBlockIds() {
        return CollectionUtil.shuffle(Lists.newArrayList(blockIds));
    }
}
