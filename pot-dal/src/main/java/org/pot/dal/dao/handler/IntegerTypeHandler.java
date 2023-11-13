package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerTypeHandler implements TypeHandler<Integer> {

    @Override
    public Integer get(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Integer param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setInt(index, param);
    }
}
