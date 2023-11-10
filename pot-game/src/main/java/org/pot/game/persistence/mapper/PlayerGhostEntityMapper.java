package org.pot.game.persistence.mapper;

import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.PlayerGhostEntity;

public class PlayerGhostEntityMapper extends SessionMapperAdapter<PlayerGhostEntity> {
    public int delete(long id) {
        String sql = "DELETE FROM `player_ghost` WHERE `player_id` =?";
        return executeUpdate(sql, id);
    }
}
