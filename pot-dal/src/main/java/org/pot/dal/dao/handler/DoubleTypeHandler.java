package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleTypeHandler implements TypeHandler<Double> {

    @Override
    public Double get(ResultSet rs, String columnName) throws SQLException {
        return rs.getDouble(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Double param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setDouble(index, param);
    }
}
