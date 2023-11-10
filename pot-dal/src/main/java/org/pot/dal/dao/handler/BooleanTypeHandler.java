package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTypeHandler implements TypeHandler<Boolean> {

    @Override
    public Boolean get(ResultSet rs, String columnName) throws SQLException {
        return rs.getBoolean(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Boolean param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setBoolean(index, param);
    }
}
