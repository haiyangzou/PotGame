package org.pot.game.persistence.mapper;

import org.pot.common.performance.memory.alloc.StringBuilderAlloc;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.StringUtil;
import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.WorldMarchEntity;

import java.util.List;

public class WordMarchEntityMapper extends SessionMapperAdapter<WorldMarchEntity> {
    public void deleteNoInidList(List<WorldMarchEntity> entities){
        if(CollectionUtil.isEmpty(entities)){
            return;
        }
        StringBuilder sql = StringBuilderAlloc.newSmallString("DELETE FROM `world_march` where `id` not in (");
        sql.append(StringUtil.join(entities,",",worldMarchEntity -> String.valueOf(worldMarchEntity.getId())));
        sql.append(")");
        executeUpdate(sql.toString());
    }
    public void deleteById(long id){
        executeUpdate("DELETE FROM `world_march` WHERE `id` =?",id);
    }
}
