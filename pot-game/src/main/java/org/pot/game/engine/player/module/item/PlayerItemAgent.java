package org.pot.game.engine.player.module.item;


import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerItemAgent extends PlayerAgentAdapter {
    private final Map<String, PlayerItemGroup> playerItemMap = new HashMap<>();
    private final List<ItemObject> changeItems = new ArrayList<>();

    public PlayerItemAgent(Player player) {
        super(player);
    }
    public int getItemCount(String itemId) {
        PlayerItemGroup playerItemGroup = this.playerItemMap.get(itemId);
        return (playerItemGroup == null) ? 0 : playerItemGroup.getAmount();
    }
    public void pushItems() {
//        PushUpdateItemS2C.Builder updateBuilder = PushUpdateItemS2C.newBuilder();
//        for (ItemObject itemObject : this.changeItems) {
//            PlayerItemGroup playerItemGroup = getPlayerItemMap().get(itemObject.getPrototype());
//            if (playerItemGroup == null || playerItemGroup.getItem(itemObject.getUuid()) == null) {
//                updateBuilder.addDeletedUuids(itemObject.getUuid());
//                continue;
//            }
//            updateBuilder.addItems(itemObject.toItemDto());
//        }
//        this.changeItems.clear();
//        if (updateBuilder.getDeletedUuidsCount() != 0 || updateBuilder.getItemsCount() != 0)
//            this.player.sendMessage( updateBuilder.build());
    }
}
