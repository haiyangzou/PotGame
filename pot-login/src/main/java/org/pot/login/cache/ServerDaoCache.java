package org.pot.login.cache;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.common.communication.server.Server;

import java.util.List;

@Mapper
public interface ServerDaoCache {
    List<Server> selectAll();

    List<Server> selectType(@Param("typeId") int typeId);

    Server selectOne(@Param("typeId") int typeId, @Param("serverId") int serverId);

}
