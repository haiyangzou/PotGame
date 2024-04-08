package org.pot.game.persistence.mapper;

import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.engine.player.module.hero.PlayerHero;
import org.pot.game.persistence.entity.PlayerHeroEntity;
import org.pot.game.persistence.entity.PlayerProfileEntity;

import java.util.List;

public class PlayerHeroEntityMapper extends SessionMapperAdapter<PlayerHeroEntity> {
    public PlayerHeroEntity select(long id) {
        String sql = "select * from `player_hero` where `id`=?";
        return executeQueryObject(sql, id);
    }
}
