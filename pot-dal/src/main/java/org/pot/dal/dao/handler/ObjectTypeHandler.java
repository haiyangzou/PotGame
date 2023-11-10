package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectTypeHandler implements TypeHandler<Object> {

    @Override
    public Object get(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Object param) throws SQLException {
        statement.setObject(index, param);
    }
}
