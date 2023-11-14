package org.pot.strategy.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.common.communication.strategy.StrategyDeviceName;

import java.util.List;

@Mapper
public interface StrategyDeviceNameDao {
    List<StrategyDeviceName> selectValidAll();
}
