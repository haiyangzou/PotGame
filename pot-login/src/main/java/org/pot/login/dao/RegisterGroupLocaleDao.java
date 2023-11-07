package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.RegisterGroupLocale;

import java.util.List;

@Mapper
public interface RegisterGroupLocaleDao {
    List<RegisterGroupLocale> selectAll();
}
