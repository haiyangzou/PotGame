package org.pot.game.persistence.mapper;

import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.PlayerCommonEntity;
import org.pot.game.persistence.entity.PlayerResourceEntity;

public class PlayerCommonEntityMapper extends SessionMapperAdapter<PlayerCommonEntity> {
    public PlayerCommonEntity select(long id) {
        String sql = "SELECT * FROM `player_common` WHERE `id`=?";
        return executeQueryObject(sql, id);
    }
}
