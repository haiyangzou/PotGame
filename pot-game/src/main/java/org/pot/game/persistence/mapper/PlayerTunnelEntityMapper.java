package org.pot.game.persistence.mapper;

import org.pot.common.performance.memory.alloc.StringBuilderAlloc;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.StringUtil;
import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.PlayerTunnelEntity;

import java.util.Collection;

public class PlayerTunnelEntityMapper extends SessionMapperAdapter<PlayerTunnelEntity> {
    public void deleteNotInKeyList(Collection<PlayerTunnelEntity> entities) {
        if (CollectionUtil.isEmpty(entities)) return;
        StringBuilder sql = StringBuilderAlloc.newSmallString("DELETE FROM `player_tunnel` WHERE `player_id` not in (");
        sql.append(StringUtil.join(entities, ",", entity -> String.valueOf(entity.getPlayerId())));
        sql.append(")");
        executeUpdate(sql.toString());
    }

    public int delete(long id) {
        String sql = "DELETE FROM `player_tunnel` WHERE `player_id`=?";
        return executeUpdate(sql, id);
    }
}
