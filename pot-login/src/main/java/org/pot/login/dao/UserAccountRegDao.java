package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.login.domain.object.UserAccountReg;

@Mapper
public interface UserAccountRegDao {
    void insertOne(@Param("userAccountReg") UserAccountReg userAccountReg);
}
