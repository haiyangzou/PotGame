package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DateTypeHandler implements TypeHandler<Date> {

    @Override
    public Date get(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }

    @Override
    public void set(PreparedStatement statement, int index, Date param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setTimestamp(index, new Timestamp(param.getTime()));
    }
}
