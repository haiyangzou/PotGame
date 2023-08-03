package org.pot.dal.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityParser<T> {
    T parse(ResultSet resultSet) throws SQLException;
}
