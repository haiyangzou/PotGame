package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.RegisterPolicyGroup;

import java.util.List;

@Mapper
public interface RegisterPolicyGroupDao {
    List<RegisterPolicyGroup> selectAll();
}
