package org.pot.strategy.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.common.communication.strategy.StrategyIp;

import java.util.List;

@Mapper
public interface StrategyIpDao {
    List<StrategyIp> selectValidAll();
}
