package org.pot.dal.dao.handler;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlDateTypeHandler implements TypeHandler<Date> {

    @Override
    public Date get(ResultSet rs, String columnName) throws SQLException {
        return rs.getDate(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Date param) throws SQLException {
        statement.setDate(index, param);
    }
}
