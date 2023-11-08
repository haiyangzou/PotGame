package org.pot.game.engine.march;

import org.pot.cache.union.UnionCaches;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;

public class Relational {
    static boolean isFriendly(long playerId1, long playerId2) {
        if (playerId1 <= 0 || playerId2 <= 0) return false;
        if (playerId1 == playerId2) return true;
        Player player1 = PlayerManager.fetchPlayer(playerId1);
        long player1UnionId;
        if (player1 != null) {
            player1UnionId = player1.unionAgent.getUnionId();
        } else {
            player1UnionId = UnionCaches.snapshot().getPlayerUnionId(playerId1);
        }
        Player player2 = PlayerManager.fetchPlayer(playerId2);
        long player2UnionId;
        if (player2 != null) {
            player2UnionId = player2.unionAgent.getUnionId();
        } else {
            player2UnionId = UnionCaches.snapshot().getPlayerUnionId(playerId2);
        }
        return player1UnionId > 0 && player1UnionId == player2UnionId;
    }
}
