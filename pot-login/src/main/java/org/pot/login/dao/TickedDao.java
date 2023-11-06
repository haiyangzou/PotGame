package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.Ticked;

@Mapper
public interface TickedDao {
    void generateKey(Ticked ticked);
}
