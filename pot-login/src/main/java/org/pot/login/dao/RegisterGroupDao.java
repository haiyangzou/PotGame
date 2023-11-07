package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.RegisterGroup;

import java.util.List;

@Mapper
public interface RegisterGroupDao {
    List<RegisterGroup> selectAll();
}
