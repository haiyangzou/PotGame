package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pot.login.entity.UserRole;

import java.util.List;

@Mapper
public interface UserRoleDao {
    UserRole selectByUid(@Param("uid") long uid);

    public List<UserRole> selectByAccountUid(long gameUid);

    void insertRoleOnExistUpdateLastLogin(@Param("userRole") UserRole userRole);

}
