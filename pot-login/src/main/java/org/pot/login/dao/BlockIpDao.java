package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.BlockIp;

import java.util.List;

@Mapper
public interface BlockIpDao {
    BlockIp selectOne(String ip);

    List<BlockIp> selectAll();

    void insert(BlockIp blockIp);

}
