package org.pot.game.persistence.mapper;

import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.PlayerProfileEntity;

import java.util.List;

public class PlayerProfileEntityMapper extends SessionMapperAdapter<PlayerProfileEntity> {
    public PlayerProfileEntity select(long id) {
        String sql = "select * from `player_profile` where `uid`=?";
        return executeQueryObject(sql, id);
    }

    public List<Long> selectAllPlayerIds() {
        String sql = "select `uid` from `player_profile`";
        return getSqlSession().executeQueryList(resultSet -> resultSet.getLong(1), sql);
    }

}
