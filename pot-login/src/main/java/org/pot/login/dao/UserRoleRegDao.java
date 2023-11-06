package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.login.entity.UserRoleReg;

@Mapper
public interface UserRoleRegDao {
    void insertOne(@Param("userRoleReg") UserRoleReg userRoleReg);
}
