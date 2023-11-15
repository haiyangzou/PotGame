package org.pot.game.persistence.mapper;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.performance.memory.alloc.StringBuilderAlloc;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.StringUtil;
import org.pot.dal.dao.SessionMapperAdapter;
import org.pot.game.persistence.entity.WorldPointEntity;

import java.util.List;

public class WorldPointEntityMapper extends SessionMapperAdapter<WorldPointEntity> {
    public void deleteNotInIdList(List<WorldPointEntity> entities) {
        if (CollectionUtil.isEmpty(entities)) {
            return;
        }
        StringBuilder sql = StringBuilderAlloc.newSmallString("DELETE FROM `world_point` WHERE `id` not in (");
        sql.append(StringUtil.join(entities, ",", worldPointEntity -> String.valueOf(worldPointEntity.getId())));
        sql.append(")");
        executeUpdate(sql.toString());
    }

    public void deleteInIdList(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        StringBuilder sql = StringBuilderAlloc.newSmallString("DELETE FROM `world_point` WHERE `id` in (");
        sql.append(StringUtils.join(ids, ","));
        sql.append(")");
        executeUpdate(sql.toString());
    }
}
