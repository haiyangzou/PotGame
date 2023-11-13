package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongTypeHandler implements TypeHandler<Long> {

    @Override
    public Long get(ResultSet rs, String columnName) throws SQLException {
        return rs.getLong(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Long param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setLong(index, param);
    }
}
