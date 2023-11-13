package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatTypeHandler implements TypeHandler<Float> {

    @Override
    public Float get(ResultSet rs, String columnName) throws SQLException {
        return rs.getFloat(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Float param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setFloat(index, param);
    }
}
