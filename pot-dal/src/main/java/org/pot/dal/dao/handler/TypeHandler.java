package org.pot.dal.dao.handler;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler<T> {
    T get(ResultSet rs, String columnName) throws SQLException;

    void set(PreparedStatement statement, int index, T param) throws SQLException;
}
