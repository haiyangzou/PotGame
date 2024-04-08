package org.pot.game.persistence.mapper;

import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.PlayerHeroEntity;
import org.pot.game.persistence.entity.PlayerResourceEntity;

public class PlayerResourceEntityMapper extends SessionMapperAdapter<PlayerResourceEntity> {
    public PlayerResourceEntity select(long id) {
        String sql = "SELECT * FROM `player_resource` WHERE `id`=?";
        return executeQueryObject(sql, id);
    }
}
