package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortTypeHandler implements TypeHandler<Short> {

    @Override
    public Short get(ResultSet rs, String columnName) throws SQLException {
        return rs.getShort(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Short param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setShort(index, param);
    }
}
