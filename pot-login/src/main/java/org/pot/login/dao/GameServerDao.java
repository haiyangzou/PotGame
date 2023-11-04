package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.common.communication.server.GameServer;

import java.util.Date;
import java.util.List;

@Mapper
public interface GameServerDao {
    List<GameServer> selectAll();

    GameServer selectOne(@Param("serverId") int serverId);

    void insertOne(@Param("gameServer") GameServer gameServer);

    void batchUpdateMaintainInfo(@Param("serverIds") List<Integer> serverIds, Date endTime, Long languageId);
}