package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.login.domain.object.UserAccount;

@Mapper
public interface UserAccountDao {
    void insertAccount(@Param("userAccount") UserAccount userAccount);

    void updateAccount(@Param("userAccount") UserAccount userAccount);

    UserAccount getUserAccountByUid(@Param("uid") long uid);

    UserAccount getUserAccountByAccount(@Param("account") String account);
}
