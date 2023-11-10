package org.pot.dal.dao;

import lombok.Getter;

@Getter
public class SqlAndParams {
    private String sql;
    private Object[] params;

    public SqlAndParams(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }
}
