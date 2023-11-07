package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.RegisterServerPolicy;

import java.util.List;

@Mapper
public interface RegisterServerPolicyDao {
    List<RegisterServerPolicy> selectAll();
}
