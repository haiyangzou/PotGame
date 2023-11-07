package org.pot.login.dao;

import org.apache.ibatis.annotations.Mapper;
import org.pot.login.entity.RegisterLocale;

import java.util.List;

@Mapper
public interface RegisterLocaleDao {
    List<RegisterLocale> selectAll();
}
