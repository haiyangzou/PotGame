package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampTypeHandler implements TypeHandler<Timestamp> {

    @Override
    public Timestamp get(ResultSet rs, String columnName) throws SQLException {
        return rs.getTimestamp(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Timestamp param) throws SQLException {
        statement.setTimestamp(index, param);
    }
}
