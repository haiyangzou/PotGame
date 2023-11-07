package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.RegisterPolicy;

import java.util.List;

@Mapper
public interface RegisterPolicyDao {
    List<RegisterPolicy> selectAll();
}
