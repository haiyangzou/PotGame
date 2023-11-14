package org.pot.strategy.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.common.communication.strategy.StrategyVersion;

import java.util.List;

@Mapper
public interface StrategyVersionDao {
    List<StrategyVersion> selectAll();
}
