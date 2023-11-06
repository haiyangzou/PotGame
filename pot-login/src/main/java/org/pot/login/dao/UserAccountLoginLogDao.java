package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.login.entity.UserAccountLoginLog;

import java.util.List;

@Mapper
public interface UserAccountLoginLogDao {
    void insertOne(@Param("userAccountLoginLog") UserAccountLoginLog userAccountLoginLog);

    void insertList(@Param("userAccountLoginLogs") List<UserAccountLoginLog> userAccountLoginLogs);

}
