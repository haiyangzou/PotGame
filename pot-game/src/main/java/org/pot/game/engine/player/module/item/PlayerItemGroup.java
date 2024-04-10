package org.pot.game.engine.player.module.item;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerItemGroup {
    private final Map<Long, ItemObject> allItems = new HashMap<>();

    private final String itemId;

    public PlayerItemGroup(String itemId) {
        this.itemId = itemId;
    }

    public int getAmount() {
        return this.allItems.values().stream().mapToInt(ItemObject::getAmount).sum();
    }
}
